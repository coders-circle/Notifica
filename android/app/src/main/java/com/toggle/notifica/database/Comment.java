package com.toggle.notifica.database;

import com.toggle.notifica.Utilities;

import org.json.JSONObject;

public class Comment extends Model {
    public long post;
    public String body;
    public long posted_at;
    public long modified_at;
    public long posted_by = -1;
    public long num_comments;

    public Comment() {}

    public Comment(JSONObject json) {
        _id = json.optLong("id", -1);
        post = json.optLong("post");
        body = json.optString("body");
        posted_at = Utilities.getDateTimeFromIso(json.optString("posted_at")).getTime();
        modified_at = Utilities.getDateTimeFromIso(json.optString("modified_at")).getTime();
        if (!json.isNull("posted_by"))
            posted_by = json.optJSONObject("posted_by").optLong("id");
        num_comments = json.optLong("num_comments");
    }
}
