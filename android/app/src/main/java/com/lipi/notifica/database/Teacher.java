package com.lipi.notifica.database;

import org.json.JSONObject;

public class Teacher extends Model {
    public long user;
    public long department;
    public String notifica_id;

    public Teacher() {}

    public Teacher(JSONObject json) {
        _id = json.optLong("id", -1);
        user = json.optLong("user_id", -1);
        department = json.optLong("department", -1);
        notifica_id = json.optString("notifica_id");
    }
}
