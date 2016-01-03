package com.lipi.notifica.database;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

// Class responsible for fetching data from server and storing them in local database
public class Client {
    public static abstract class Callback {
        public int queue = 0; // minimum number of callbacks that are in queue besides current one
        public abstract void refresh();
    }

    private DbHelper mDbHelper;
    private String mUsername = "069bct548", mPassword = "adityakhatri";

    public Client(Context context) {
        mDbHelper = new DbHelper(context);
    }

    private void addPeriod(JSONObject json, Callback callback) throws JSONException {
        Period p = new Period(json);
        p.save(mDbHelper);

        getSubject(json.getLong("subject"), callback);
        JSONArray tsJson = json.getJSONArray("teachers");
        if (tsJson != null) {
            for (int i = 0; i < tsJson.length(); ++i) {
                PeriodTeacher periodTeacher = new PeriodTeacher(p._id, tsJson.getLong(i));
                periodTeacher.save(mDbHelper);
                getTeacher(tsJson.getLong(i), callback);
            }
        }

        JSONArray gsJson = json.getJSONArray("groups");
        if (gsJson != null) {
            for (int i = 0; i < gsJson.length(); ++i) {
                PeriodGroup periodGroup = new PeriodGroup(p._id, gsJson.getLong(i));
                periodGroup.save(mDbHelper);
                getGroup(gsJson.getLong(i), callback);
            }
        }
    }

    private void addSubject(JSONObject json, Callback callback) {
        Subject s = new Subject(json);
        s.save(mDbHelper);

        // getDepartment(s.department);
    }

    private void addTeacher(JSONObject json, Callback callback) {
        Teacher t = new Teacher(json);
        t.save(mDbHelper);
        getUser(t.user, callback);
        // getDepartment(s.department);
    }

    private void addStudent(JSONObject json, Callback callback) {
        Student s = new Student(json);
        s.save(mDbHelper);
        getUser(s.user, callback);
        getGroup(s.p_group, callback);
    }

    private void addUser(JSONObject json, Callback callback) {
        User u = new User(json);
        u.save(mDbHelper);
    }

    private void addGroup(JSONObject json, Callback callback) {
        PGroup g = new PGroup(json);
        g.save(mDbHelper);
        getClass(g.p_class, callback);
    }

    private void addClass(JSONObject json, Callback callback) {
        PClass c = new PClass(json);
        c.save(mDbHelper);
        // getDepartment(c.p_class);
    }

    // Get the routine for this user
    public void getRoutine(final Callback callback) {
        if (callback != null)
            callback.queue++;

        NetworkHandler handler = new NetworkHandler(mUsername, mPassword, true);
        handler.get("routine/api/v1/periods/", new NetworkHandler.Callback() {
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
                            addPeriod(periods.getJSONObject(i), callback);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (callback != null) {
                    callback.queue--;
                    callback.refresh();
                }
            }
        });
    }

    // Get a subject
    public void getSubject(final long id, final Callback callback) {
        // Add only if it doesn't exist
        if (Subject.count(Subject.class, mDbHelper, "_id=?", new String[]{id + ""}) > 0)
            return;

        if (callback != null)
            callback.queue++;

        NetworkHandler handler = new NetworkHandler(mUsername, mPassword, true);
        handler.get("classroom/api/v1/subjects/"+id+"/", new NetworkHandler.Callback() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // TODO: Delete it if it already exists
                    // Subject.delete(Subject.class, mDbHelper, "id=?", new String[]{id + ""});

                    // Then add the new one fetched from server
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;
                        addSubject(json, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (callback != null) {
                        callback.queue--;
                        callback.refresh();
                    }
                }
            }
        });
    }

    // Get a teacher
    public void getTeacher(final long id, final Callback callback) {
        // Add only if it doesn't exist
        if (Teacher.count(Teacher.class, mDbHelper, "_id=?", new String[]{id + ""}) > 0)
            return;

        if (callback != null)
            callback.queue++;

        NetworkHandler handler = new NetworkHandler(mUsername, mPassword, true);
        handler.get("classroom/api/v1/teachers/"+id+"/", new NetworkHandler.Callback() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // TODO: Delete it if it already exists
                    // Teacher.delete(Teacher.class, mDbHelper, "id=?", new String[]{id+""});

                    // Then add the new one fetched from server
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;
                        addTeacher(json, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (callback != null) {
                        callback.queue--;
                        callback.refresh();
                    }
                }
            }
        });
    }

    // Get a student
    public void getStudent(final long id, final Callback callback) {
        // Add only if it doesn't exist
        if (Student.count(Student.class, mDbHelper, "_id=?", new String[]{id + ""}) > 0)
            return;

        if (callback != null)
            callback.queue++;

        NetworkHandler handler = new NetworkHandler(mUsername, mPassword, true);
        handler.get("classroom/api/v1/students/"+id+"/", new NetworkHandler.Callback() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // TODO: Delete it if it already exists
                    // Student.delete(Student.class, mDbHelper, "id=?", new String[]{id+""});

                    // Then add the new one fetched from server
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;
                        addStudent(json, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (callback != null) {
                        callback.queue--;
                        callback.refresh();
                    }
                }
            }
        });
    }

    // Get a user
    public void getUser(final long id, final Callback callback) {
        // Add only if it doesn't exist
        if (User.count(User.class, mDbHelper, "_id=?", new String[]{id + ""}) > 0)
            return;

        if (callback != null)
            callback.queue++;

        NetworkHandler handler = new NetworkHandler(mUsername, mPassword, true);
        handler.get("classroom/api/v1/users/"+id+"/", new NetworkHandler.Callback() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // TODO: Delete it if it already exists
                    // User.delete(User.class, mDbHelper, "id=?", new String[]{id+""});

                    // Then add the new one fetched from server
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;
                        addUser(json, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (callback != null) {
                        callback.queue--;
                        callback.refresh();
                    }
                }
            }
        });
    }

    // Get a group
    public void getGroup(final long id, final Callback callback) {
        // Add only if it doesn't exist
        if (PGroup.count(PGroup.class, mDbHelper, "_id=?", new String[]{id + ""}) > 0)
            return;

        if (callback != null)
            callback.queue++;

        NetworkHandler handler = new NetworkHandler(mUsername, mPassword, true);
        handler.get("classroom/api/v1/groups/"+id+"/", new NetworkHandler.Callback() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // TODO: Delete it if it already exists
                    // PGroup.delete(PGroup.class, mDbHelper, "id=?", new String[]{id+""});

                    // Then add the new one fetched from server
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;
                        addGroup(json, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (callback != null) {
                        callback.queue--;
                        callback.refresh();
                    }
                }
            }
        });
    }

    // Get a class
    public void getClass(final long id, final Callback callback) {
        // Add only if it doesn't exist
        if (PClass.count(PClass.class, mDbHelper, "_id=?", new String[]{id + ""}) > 0)
            return;

        if (callback != null)
            callback.queue++;

        NetworkHandler handler = new NetworkHandler(mUsername, mPassword, true);
        handler.get("classroom/api/v1/classes/"+id+"/", new NetworkHandler.Callback() {
            @Override
            public void onComplete(NetworkHandler.Result result) {
                if (result.success) {
                    // TODO: Delete it if it already exists
                    // PClass.delete(PClass.class, mDbHelper, "id=?", new String[]{id+""});

                    // Then add the new one fetched from server
                    try {
                        JSONObject json = new JSONObject(result.result);
                        if (json.has("detail") && json.getString("detail").equals("Not found."))
                            return;
                        addClass(json, callback);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                if (callback != null) {
                    callback.queue--;
                    callback.refresh();
                }
            }
        });
    }
}
