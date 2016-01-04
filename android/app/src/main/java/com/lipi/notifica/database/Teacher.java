package com.lipi.notifica.database;

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
}
