package com.lipi.notifica.database;

import org.json.JSONObject;

public class PGroup extends Model {
    public long p_class;
    public String group_id;

    public PGroup() {}

    public PGroup(JSONObject json) {
        _id = json.optLong("id", -1);
        group_id = json.optString("group_id");
        p_class = json.optLong("p_class");
    }
}
