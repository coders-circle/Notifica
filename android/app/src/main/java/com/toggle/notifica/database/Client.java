package com.toggle.notifica.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.toggle.notifica.Utilities;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
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

        getRoutine(json.getLong("routine"), clientListener);

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

        getElectives(s._id, clientListener);
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
        getDepartment(c.department, clientListener);

        getProfile(c.profile, clientListener);
    }

    private void addDepartment(JSONObject json, ClientListener clientListener) {
        Department d = new Department(json);
        d.save(mDbHelper);
        // TODO: getOrganization(d.department, clientListener);

        getProfile(d.profile, clientListener);
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

    private void addRoutine(JSONObject json, ClientListener clientListener) throws JSONException {
        Routine r = new Routine(json);
        r.save(mDbHelper);

        getClass(json.getLong("p_class"), clientListener);
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
                    Routine.deleteAll(Routine.class, mDbHelper);

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

    // Get a routine
    public void getRoutine(final long id, final ClientListener clientListener) {
        get(clientListener, "routine", id, "routine/api/v1/routines/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException {
                        addRoutine(json, clientListener);
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

    // Get a class
    public void getDepartment(final long id, final ClientListener clientListener) {
        get(clientListener, "department", id, "classroom/api/v1/departments/" + id + "/",
                new Callback() {
                    @Override
                    public void callback(JSONObject json) throws JSONException {
                        addDepartment(json, clientListener);
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
    public void getPosts(long offset, long count, long time, long profile, final ClientListener clientListener) {
        if (clientListener != null) {
            if (clientListener.queue.contains("posts"))
                return;
            clientListener.queue.add("posts");
        }

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
        if (profile >= 0)
            query += (query.equals("")?"":"&") + "profile="+profile;

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



    // Get comments from server
    public void getComments(final long postId, final ClientListener clientListener) {
        if (clientListener != null) {
            if (clientListener.queue.contains("comments:"+postId))
                return;
            clientListener.queue.add("comments:" + postId);
        }

        // Get comments
        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.get("feed/api/v1/comments/?postid="+postId, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // Delete previous comments for this post
                    Comment.delete(Comment.class, mDbHelper, "post=?", new String[]{""+postId});

                    // Now add each comment fetched from server
                    try {
                        JSONArray comments = new JSONArray(result.result);
                        for (int i = 0; i < comments.length(); ++i) {
                            addComment(comments.getJSONObject(i), clientListener);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (clientListener != null) {
                    clientListener.queue.remove("comments:"+postId);
                    clientListener.refresh();
                }
            }
        });
    }

    // Post comment
    public void postComment(final String comment, final long postId, final ClientListener clientListener) {
        JSONObject data = new JSONObject();
        try {
            data.put("post", postId);
            data.put("body", comment);
            data.put("links", "[]");
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.post("feed/api/v1/comments/", data.toString(), new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    try {
                        addComment(new JSONObject(result.result), clientListener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    clientListener.refresh();
                } else {
                    Toast.makeText(mContext, "Couldn't post comment.\nCheck internet connection and try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Post post
    public void postPost(final String title, final String content,
                         final long profile, final ClientListener clientListener) {

        JSONObject data = new JSONObject();
        try {
            data.put("title", title);
            data.put("body", content);
            data.put("tags", "");
            data.put("links", "[]");
            data.put("profile", profile);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.post("feed/api/v1/posts/", data.toString(), new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    try {
                        addPost(new JSONObject(result.result), clientListener);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    clientListener.refresh();
                } else {
                    Toast.makeText(mContext, "Couldn't add post."+
                                    "\nCheck internet connection and try again.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void getAssociated(final String type, long userId, final ClientListener clientListener) {
        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.get("classroom/api/v1/" + type + "s/?user=" + userId, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    try {
                        JSONArray list = new JSONArray(result.result);
                        for (int i = 0; i < list.length(); ++i) {
                            if (type.equals("teacher"))
                                addTeacher(list.getJSONObject(i), clientListener);
                            else if (type.equals("student"))
                                addStudent(list.getJSONObject(i), clientListener);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                clientListener.refresh();
            }
        });
    }

    public void getElectives(final long subject, final ClientListener clientListener) {
        if (clientListener != null) {
            if (clientListener.queue.contains("electives:" + subject))
                return;
            clientListener.queue.add("electives:" + subject);
        }

        Student student = User.getLoggedInUser(mContext).getStudent(mDbHelper);
        final long studentId = (student!=null)?student._id:-1;

        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.get("classroom/api/v1/electives/?subject=" + subject, new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    try {
                        JSONArray list = new JSONArray(result.result);
                        for (int i=0; i<list.length(); ++i) {
                            JSONObject electiveJson = list.getJSONObject(i);
                            Elective elective = new Elective(electiveJson, studentId);
                            elective.save(mDbHelper);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (clientListener != null) {
                    clientListener.queue.remove("electives:"+subject);
                    clientListener.refresh();
                }
            }
        });
    }

    public void selectElective(final Elective elective, final ClientListener clientListener) {
        NetworkHandler handler = new NetworkHandler(mContext, mUsername, mPassword, true);
        handler.post("classroom/elective/select/"+elective._id+"/", "",
                new NetworkHandler.NetworkListener() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    try {
                        Log.d("result", result.result);
                        JSONObject message = new JSONObject(result.result);
                        if (message.has("result") &&
                                message.getString("result").equals("success")) {

                            // On success refresh the electives for this class
                            List<Subject> subjects = PClass.get(PClass.class,
                                    mDbHelper, elective.p_class).getSubjects(mDbHelper);
                            for (Subject s: subjects)
                                getElectives(s._id, clientListener);

                            clientListener.refresh();
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                Toast.makeText(mContext, "Couldn't connect to server.\n"+
                        "Make sure you are connected to internet to perform this action.",
                        Toast.LENGTH_SHORT).show();
                clientListener.refresh();
            }
        });
    }
}
