package com.lipi.notifica.database;

import android.util.Log;

import com.lipi.notifica.Utilities;

import org.json.JSONObject;

public class Post extends Model {
    public String title;
    public String body;
    public long posted_at;
    public long modified_at;
    public long posted_by = -1;
    public String tags;
    public long num_comments;
    public long profile;

    public Post() {}

    public Post(JSONObject json) {
        _id = json.optLong("id", -1);
        title = json.optString("title");
        body = json.optString("body");
        posted_at = Utilities.getDateTimeFromIso(json.optString("posted_at")).getTime();
        modified_at = Utilities.getDateTimeFromIso(json.optString("modified_at")).getTime();
        if (!json.isNull("posted_by"))
            posted_by = json.optJSONObject("posted_by").optLong("id");
        num_comments = json.optLong("num_comments");
        tags = json.optString("tags");
        profile = json.optLong("profile");
    }
}
