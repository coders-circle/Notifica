package com.lipi.notifica.database;

import org.json.JSONObject;

import java.util.Calendar;

public class Profile extends Model {
    public String avatar;
    public long downloaded_at;

    public Profile() {}

    public Profile(JSONObject json) {
        _id = json.optLong("id", -1);
        avatar = json.optString("avatar");
        downloaded_at = Calendar.getInstance().getTimeInMillis();
    }
}
