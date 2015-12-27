package com.lipi.notifica.database;

import org.json.JSONObject;

public class User extends Model {
    public String first_name;
    public String last_name;
    public String username;
    public String email;

    public User() {}

    public User(JSONObject json) {
        _id = json.optLong("id", -1);
        first_name = json.optString("first_name");
        last_name = json.optString("last_name");
        username = json.optString("username");
        email = json.optString("email");
    }
}
