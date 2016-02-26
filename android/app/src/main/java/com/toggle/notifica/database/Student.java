package com.toggle.notifica.database;

import org.json.JSONObject;

public class Student extends Model {
    public long user;
    public long p_group;

    public Student() {}

    public Student(JSONObject json) {
        _id = json.optLong("id", -1);
        user = json.optLong("user_id", -1);
        p_group = json.optLong("group", -1);
    }

    public PGroup getGroup(DbHelper dbHelper) {
        return PGroup.get(PGroup.class, dbHelper, p_group);
    }
}