package com.toggle.notifica.database;

import org.json.JSONObject;

import java.util.List;

public class Routine extends Model {
    public long p_class;

    public Routine() {}

    public Routine(JSONObject json) {
        _id = json.optLong("id", -1);
        p_class = json.optLong("p_class");
    }

    public List<Period> getPeriods(DbHelper helper) {
        return Period.query(Period.class, helper, "routine=?", new String[]{_id+""},
                null, null, null);
    }
}
