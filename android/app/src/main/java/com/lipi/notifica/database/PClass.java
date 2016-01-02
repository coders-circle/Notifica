package com.lipi.notifica.database;

import org.json.JSONObject;

public class PClass extends Model {
    public String class_id;
    public long department;

    public PClass() {}

    public PClass(JSONObject json) {
        _id = json.optLong("id", -1);
        class_id = json.optString("class_id");
        department = json.optLong("department");
    }
}
