package com.lipi.notifica.database;

import org.json.JSONObject;

public class Department extends Model {
    public String name;
    public long organization;
    public long profile;

    public Department() {}

    public Department(JSONObject json) {
        _id = json.optLong("id", -1);
        name = json.optString("name");
        organization = json.optLong("organization", -1);
        profile = json.optLong("profile");
    }
}
