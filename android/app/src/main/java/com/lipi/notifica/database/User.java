package com.lipi.notifica.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONObject;

public class User extends Model {
    public String first_name;
    public String last_name;
    public String username;
    public String email;
    public long profile;

    public User() {}

    public User(JSONObject json) {
        _id = json.optLong("id", -1);
        first_name = json.optString("first_name");
        last_name = json.optString("last_name");
        username = json.optString("username");
        email = json.optString("email");
        profile = json.optLong("profile");
    }

    public String getName() {
        if (first_name.equals(""))
            return username;
        else
            return first_name + " " + last_name;
    }

    // Get the logged in user
    public static User getLoggedInUser(Context context) {
        DbHelper helper = new DbHelper(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return User.get(User.class, helper, "username=?", new String[]{preferences.getString("username", "")}, null);
    }
}
