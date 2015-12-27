package com.lipi.notifica.database;

import org.json.JSONObject;

public class Department extends Model {
    public String name;
    public String notifica_id;
    public long organization;

    public Department() {}

    public Department(JSONObject json) {
        _id = json.optLong("id", -1);
        name = json.optString("name");
        organization = json.optLong("organization", -1);
        notifica_id = json.optString("notifica_id");
    }
}
