package com.lipi.notifica.database;

import org.json.JSONObject;

public class Student extends Model {
    public long user;
    public long p_group;
    public String notifica_id;

    public Student() {}

    public Student(JSONObject json) {
        _id = json.optLong("id", -1);
        user = json.optLong("user_id", -1);
        p_group = json.optLong("group", -1);
        notifica_id = json.optString("notifica_id");
    }
}