package com.toggle.notifica.database;

import org.json.JSONObject;

public class Teacher extends Model {
    public long user = -1;
    public String username = "";
    public long department;

    public Teacher() {}

    public Teacher(JSONObject json) {
        _id = json.optLong("id", -1);
        if (!json.isNull("user_id"))
            user = json.optLong("user_id", -1);
        if (!json.isNull("username"))
            username = json.optString("username");
        department = json.optLong("department", -1);
    }

    public Department getDepartment(DbHelper dbHelper) {
        return Department.get(Department.class, dbHelper, department);
    }

    public User getUser(DbHelper dbHelper) {
        if (user < 0)
            return null;
        return User.get(User.class, dbHelper, user);
    }

    public String getUsername(DbHelper dbHelper) {
        // Teacher may or may not have a user account
        User user = getUser(dbHelper);

        if(user != null)
            return user.getName();
        return username;
    }
}
