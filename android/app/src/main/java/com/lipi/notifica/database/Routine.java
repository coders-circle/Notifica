package com.lipi.notifica.database;

import org.json.JSONObject;

public class Routine extends Model {
    public long p_class;

    public Routine() {}

    public Routine(JSONObject json) {
        _id = json.optLong("id", -1);
        p_class = json.optLong("p_class");
    }
}
