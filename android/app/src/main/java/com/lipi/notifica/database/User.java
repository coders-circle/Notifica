package com.lipi.notifica.database;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import org.json.JSONObject;

import java.util.List;

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
        //DbHelper helper = new DbHelper(context);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        User user = new User();
        user._id = preferences.getLong("id", -1);
        user.first_name = preferences.getString("first_name", "");
        user.last_name = preferences.getString("last_name", "");
        user.email = preferences.getString("email", "");
        user.profile = preferences.getLong("profile", -1);
        return user;
    }

    public Student getStudent(DbHelper dbHelper) {
        return Student.get(Student.class, dbHelper, "user=?", new String[]{""+_id}, null);
    }

    public List<Teacher> getTeachers(DbHelper dbHelper) {
        return Teacher.query(Teacher.class, dbHelper, "user=?", new String[]{""+_id}, null, null, null);
    }
}
