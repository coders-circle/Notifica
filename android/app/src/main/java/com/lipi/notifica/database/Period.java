package com.lipi.notifica.database;

import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.List;

public class Period extends Model {
    public long subject;
    public int start_time;
    public int end_time;
    public int day;
    public String remarks;

    public Period() {}

    public Period(JSONObject json) {
        _id = json.optLong("id", -1);
        subject = json.optLong("subject");
        start_time = json.optInt("start_time");
        end_time = json.optInt("end_time");
        day = json.optInt("day");
        remarks = json.optString("remarks");
    }

    public List<PGroup> getGroups(SQLiteOpenHelper helper) {
        return PGroup.query(PGroup.class, helper, "period=?", new String[]{""+_id}, null, null, null);
    }
}
