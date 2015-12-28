package com.lipi.notifica.database;

import org.json.JSONObject;

public class Organization extends Model {
    public String name;
    public String notifica_id;

    public Organization() {}

    public Organization(JSONObject json) {
        _id = json.optLong("id", -1);
        name = json.optString("name");
        notifica_id = json.optString("notifica_id");
    }
}
