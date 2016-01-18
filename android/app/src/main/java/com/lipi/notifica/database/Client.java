package com.lipi.notifica.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.preference.PreferenceManager;
import android.util.Log;

import com.lipi.notifica.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

// Class responsible for fetching data from server and storing them in local database
public class Client {

    public static abstract class ClientListener {
        public List<String> queue = new ArrayList<>();  // Queue of current being fetched objects
        public abstract void refresh();
    }

    private final DbHelper mDbHelper;
    private final Context mContext;
    private final String mUsername, mPassword;

    public Client(Context context) {
        mContext = context;
        mDbHelper = new DbHelper(context);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        mUsername = preferences.getString("username", "");
        mPassword = preferences.getString("password", "");
    }

    public Client(Context context, String username, String password) {
        mContext = context;
        mDbHelper = new DbHelper(context);
        mUsername = username;
        mPassword = password;
    }

    private void addPeriod(JSONObject json, ClientListener clientListener) throws JSONException {
        Period p = new Period(json);
        p.save(mDbHelper);

        getSubject(p.subject, clientListener);
        JSONArray tsJson = json.getJSONArray("teachers");
        if (tsJson != null) {
            for (int i = 0; i < tsJson.length(); ++i) {
                PeriodTeacher periodTeacher = new PeriodTeacher(p._id, tsJson.getLong(i));
                periodTeacher.save(mDbHelper);
                getTeacher(tsJson.getLong(i), clientListener);
            }
        }

        JSONArray gsJson = json.getJSONArray("groups");
        if (gsJson != null) {
            for (int i = 0; i < gsJson.length(); ++i) {
                PeriodGroup periodGroup = new PeriodGroup(p._id, gsJson.getLong(i));
                periodGroup.save(mDbHelper);
                getGroup(gsJson.getLong(i), clientListener);
            }
        }
    }

    private void addSubject(JSONObject json, ClientListener clientListener) {
        Subject s = new Subject(json);
        s.save(mDbHelper);
        // getDepartment(s.department);
    }

    private void addTeacher(JSONObject json, ClientListener clientListener) throws JSONException {
        Teacher t = new Teacher(json);
        t.save(mDbHelper);

        if (!json.isNull("user"))
            addUser(json.getJSONObject("user"), clientListener);
        // getDepartment(s.department);
    }

    private void addStudent(JSONObject json, ClientListener clientListener) throws JSONException {
        Student s = new Student(json);
        s.save(mDbHelper);

        addUser(json.getJSONObject("user"), clientListener);
        getGroup(s.p_group, clientListener);
    }

    public void addUser(JSONObject json, ClientListener clientListener) {
        User u = new User(json);
        u.save(mDbHelper);

        getProfile(u.profile, clientListener);
    }

    private void addGroup(JSONObject json, ClientListener clientListener) {
        PGroup g = new PGroup(json);
        g.save(mDbHelper);
        getClass(g.p_class, clientListener);
    }

    private void addClass(JSONObject json, ClientListener clientListener) {
        PClass c = new PClass(json);
        c.save(mDbHelper);
        // getDepartment(c.p_class);
    }

    private void addPost(JSONObject json, ClientListener clientListener) throws JSONException {
        Post p = new Post(json);
        p.save(mDbHelper);

        addUser(json.getJSONObject("posted_by"), clientListener);
    }

    private void addComment(JSONObject json, ClientListener clientListener) throws JSONException {
        Comment c = new Comment(json);
        c.save(mDbHelper);

        addUser(json.getJSONObject("posted_by"), clientListener);
    }

    // Get the routine for this user
    public void getRoutine(final ClientListener clientListener) {
        if (clientListener != null) {
            if (clientListener.queue.contains("routine"))
                return;
            clientListener.queue.add("routine");
        }

        // Get periods
        final NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.get("routine/api/v1/periods/", new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // First delete all periods currently stored
                    Period.deleteAll(Period.class, mDbHelper);
                    PeriodTeacher.deleteAll(PeriodTeacher.class, mDbHelper);
                    PeriodGroup.deleteAll(PeriodGroup.class, mDbHelper);

                    // Then add each period fetched from server
                    try {
                        JSONArray periods = new JSONArray(result.result);
                        for (int i = 0; i < periods.length(); ++i) {
                            addPeriod(periods.getJSONObject(i), clientListener);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // also get the electives
                handler.get("routine/api/v1/electives/", new NetworkHandler.NetworkListener() {
                    @Override
                    public void onComplete(NetworkHandler.Result result) {
                        if (result.success) {
                            // Add each elective fetched from server
                            try {
                                JSONArray periods = new JSONArray(result.result);
                                for (int i = 0; i < periods.length(); ++i) {
                                    JSONObject json = periods.getJSONObject(i);

                                    addPeriod(json, clientListener);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (clientListener != null) {
                            clientListener.queue.remove("routine");
                            clientListener.refresh();
                        }
                    }
                });
            }
        });
    }


    private interface Callback {
        void callback(JSONObject json) throws JSONException;
    }

    // A generic get function that is called by following functions
    private void get(final ClientListener clientListener, final String name, final long id,
                     final String url, final Callback callback) {
        if (clientListener != null) {
            if (clientListener.queue.contains(name+":"+id))
                return;
            clientListener.queue.add(name+":"+id);
        }

        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.get(url, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // Add the new one fetched from server
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;
                        callback.callback(json);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (clientListener != null) {
                        clientListener.queue.remove(name+":"+id);
                        clientListener.refresh();
                    }
                }
            }
        });
    }

    // Get a subject
    public void getSubject(final long id, final ClientListener clientListener) {
        get(clientListener, "subject", id, "classroom/api/v1/subjects/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException {
                        addSubject(json, clientListener);
                    }
                });
    }

    // Get a teacher
    public void getTeacher(final long id, final ClientListener clientListener) {
        get(clientListener, "teacher", id, "classroom/api/v1/teachers/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException{
                        addTeacher(json, clientListener);
                    }
                });
    }

    // Get a student
    public void getStudent(final long id, final ClientListener clientListener) {
        get(clientListener, "student", id, "classroom/api/v1/students/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException {
                        addStudent(json, clientListener);
                    }
                });
    }

    // Get a user
    public void getUser(final long id, final ClientListener clientListener) {
        get(clientListener, "user", id, "classroom/api/v1/users/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException {
                        addUser(json, clientListener);
                    }
                });
    }

    // Get a group
    public void getGroup(final long id, final ClientListener clientListener) {
        get(clientListener, "group", id, "classroom/api/v1/groups/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException {
                        addGroup(json, clientListener);
                    }
                });
    }

    // Get a class
    public void getClass(final long id, final ClientListener clientListener) {
        get(clientListener, "class", id, "classroom/api/v1/classes/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException {
                        addClass(json, clientListener);
                    }
                });
    }


    private static class TempClass {
        public Object object;
    }
    // Get a profile
    public void getProfile(final long id, final ClientListener clientListener) {

        if (clientListener != null) {
            if (clientListener.queue.contains("profile:"+id))
                return;
            clientListener.queue.add("profile:"+id);
        }

        final TempClass oldAvatar = new TempClass();
        oldAvatar.object = null;
        // If profile already exists, refresh once
        if (Profile.count(Profile.class, mDbHelper, "_id=?", new String[]{id+""}) > 0) {
            oldAvatar.object = Profile.get(Profile.class, mDbHelper, id).avatar_data;
            if (clientListener != null)
                clientListener.refresh();
        }

        // Also get new one from server and refresh again
        final NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.get("classroom/api/v1/profiles/" + id + "/", new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // Save the fetched profile
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;

                        // Also save the avatar of the profile
                        final Profile p = new Profile(json);
                        if (oldAvatar.object != null)
                            p.avatar_data = (byte[])oldAvatar.object;
                        p.save(mDbHelper);

                        if (clientListener != null)
                            clientListener.queue.add("profile_avatar:" + p.avatar);

                        handler.getImage(p.avatar, new NetworkHandler.NetworkListener() {
                            @Override
                            public void onComplete(NetworkHandler.Result result) {
                                p.setAvatar(((NetworkHandler.ImageResult) result).bitmap);
                                p.save(mDbHelper);
                                if (clientListener != null) {
                                    clientListener.queue.remove("profile_avatar:" + p.avatar);
                                    clientListener.refresh();
                                }
                            }
                        });

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (clientListener != null) {
                    clientListener.queue.remove("profile:" + id);
                    clientListener.refresh();
                }
            }
        });
    }

    // Get posts from server, offset, count and time can be -1 if not needed
    public void getPosts(long offset, long count, long time, final ClientListener clientListener) {
        if (clientListener != null)
            clientListener.queue.add("posts");

        // Get posts
        String query = "";
        if (offset >= 0)
            query +=  "offset="+offset;
        if (count >= 0)
            query += (query.equals("")?"":"&") + "count="+count;
        if (time >= 0)
            try {
                query += (query.equals("")?"":"&") + "time="+ URLEncoder.encode(Utilities.formatDateTimeToIso(time), "utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.get("feed/api/v1/posts/" + (query.equals("")?"":"?"+query), new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // Add each post fetched from server
                    try {
                        JSONArray posts = new JSONArray(result.result);
                        for (int i = 0; i < posts.length(); ++i) {
                            addPost(posts.getJSONObject(i), clientListener);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (clientListener != null) {
                    clientListener.queue.remove("posts");
                    clientListener.refresh();
                }
            }
        });
    }
}
