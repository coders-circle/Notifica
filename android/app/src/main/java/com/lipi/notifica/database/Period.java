package com.lipi.notifica.database;

import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;
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
        List<PeriodGroup> periodGroups =  PeriodGroup.query(PeriodGroup.class, helper, "period=?", new String[]{""+_id}, null, null, null);
        List<PGroup> pGroups = new ArrayList<>();
        for (PeriodGroup periodGroup : periodGroups) {
            pGroups.addAll(PGroup.query(PGroup.class, helper, "_id=?", new String[]{""+periodGroup.p_group}, null, null, null));
        }
        return pGroups;
    }

    public List<Teacher> getTeachers(SQLiteOpenHelper helper) {
        List<PeriodTeacher> periodTeachers =  PeriodTeacher.query(PeriodTeacher.class, helper, "period=?", new String[]{""+_id}, null, null, null);
        List<Teacher> teachers = new ArrayList<>();
        for (PeriodTeacher periodTeacher : periodTeachers) {
            teachers.addAll(Teacher.query(Teacher.class, helper, "_id=?", new String[]{""+periodTeacher.teacher}, null, null, null));
        }
        return teachers;
    }

    public static String intToTime(int time) {
        int hrs = time / 60;
        int min = time % 60;
        return String.format("%02d:%02d", hrs, min);
    }

    public String getStartTime() {
        return intToTime(start_time);
    }

    public String getEndTime() {
        return intToTime(end_time);
    }
}
