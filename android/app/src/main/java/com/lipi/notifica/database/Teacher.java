package com.lipi.notifica.database;

import org.json.JSONObject;

public class Teacher extends Model {
    public long user;
    public long department;

    public Teacher() {}

    public Teacher(JSONObject json) {
        _id = json.optLong("id", -1);
        user = json.optLong("user_id", -1);
        department = json.optLong("department", -1);
    }
}
