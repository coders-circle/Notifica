package com.lipi.notifica.database;

import org.json.JSONObject;

import java.util.List;

public class PClass extends Model {
    public String class_id;
    public String description;
    public long department;
    public long profile;

    public PClass() {}

    public PClass(JSONObject json) {
        _id = json.optLong("id", -1);
        class_id = json.optString("class_id");
        description = json.optString("description");
        department = json.optLong("department", -1);
        profile = json.optLong("profile");
    }

    public Department getDepartment(DbHelper dbHelper) {
        if (department < 0)
            return null;
        return Department.get(Department.class, dbHelper, department);
    }

    public List<Routine> getRoutines(DbHelper dbHelper) {
        return Routine.query(Routine.class, dbHelper, "p_class=?", new String[]{_id+""},
                null, null, null);
    }
}
