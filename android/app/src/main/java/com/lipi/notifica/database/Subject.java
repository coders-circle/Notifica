package com.lipi.notifica.database;

import org.json.JSONObject;

public class Subject extends Model {
    public String name;
    public String notifica_id;
    public long department;

    public Subject() {}

    public Subject(JSONObject json) {
        _id = json.optLong("id", -1);
        name = json.optString("name");
        department = json.optLong("department", -1);
        notifica_id = json.optString("notifica_id");
    }
}
