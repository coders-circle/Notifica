package com.toggle.notifica.database;

import org.json.JSONObject;

import java.util.ArrayList;
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
        return Routine.query(Routine.class, dbHelper, "p_class=?", new String[]{_id + ""},
                null, null, null);
    }

    public List<Subject> getSubjects(DbHelper dbHelper) {
        List<Long> sids = new ArrayList<>();
        List<Routine> routines = getRoutines(dbHelper);
        List<Subject> subjects = new ArrayList<>();

        for (Routine r: routines) {
            List<Period> periods = r.getPeriods(dbHelper);
            for (Period p: periods) {
                if (!sids.contains(p.subject)) {
                    sids.add(p.subject);
                    Subject s = p.getSubject(dbHelper);
                        subjects.add(s);
                }
            }
        }
        return subjects;
    }

    public boolean checkIfAdmin(DbHelper dbHelper, long userId) {
        return ClassAdmin.count(ClassAdmin.class, dbHelper, "p_class=? AND user=?",
                new String[]{_id+"", userId+""}) > 0;
    }
}
