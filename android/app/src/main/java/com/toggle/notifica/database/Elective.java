package com.toggle.notifica.database;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

public class Elective extends Model {
    public long subject;
    public boolean selected = false;
    public long p_class;
    public String p_group = "";

    public Elective() {}

    public Elective(JSONObject json, long student) {
        _id = json.optLong("id", -1);
        subject = json.optLong("subject", -1);
        p_class = json.optLong("p_class", -1);
        p_group = json.optString("elective_group", "");
        selected = false;

        if (student >= 0) {
            JSONArray students = json.optJSONArray("students");
            if (students != null)
                for (int i = 0; i < students.length(); ++i)
                    if (students.optLong(i, -1) == student) {
                        selected = true;
                        break;
                    }
        }
    }

    public Subject getSubject(DbHelper dbHelper) {
        return Subject.get(Subject.class, dbHelper, subject);
    }

    public interface SelectCallback {
        void onSelectionComplete();
    }

    public void select(Context context, final SelectCallback callback) {
        new Client(context).selectElective(this, new Client.ClientListener() {
            @Override
            public void refresh(boolean success) {
                if (queue.size() == 0) {
                    callback.onSelectionComplete();
                }
            }
        });
        /*// Get all other electives in this group and set them unselected
        List<Elective> electives = Elective.query(Elective.class, dbHelper,
                "p_class=? AND p_group=?", new String[]{p_class+"", p_group}, null, null, null);
        for (Elective elective: electives) {
            elective.selected = false;
            elective.save(dbHelper);
        }

        selected = true;
        save(dbHelper);
        */
    }
}
