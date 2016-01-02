package com.lipi.notifica.database;

import org.json.JSONObject;

public class Subject extends Model {
    public String name;
    public String short_name;
    public long department;

    public Subject() {}

    public Subject(JSONObject json) {
        _id = json.optLong("id", -1);
        name = json.optString("name");
        short_name = json.optString("short_name");
        department = json.optLong("department", -1);
    }
}
