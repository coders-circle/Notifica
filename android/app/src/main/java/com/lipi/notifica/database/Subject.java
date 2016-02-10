package com.lipi.notifica.database;

import org.json.JSONObject;

public class Subject extends Model {
    public String name;
    public String short_name;
    public long department;
    public String color;

    public Subject() {}

    public Subject(JSONObject json) {
        _id = json.optLong("id", -1);
        name = json.optString("name");
        short_name = json.optString("short_name");
        department = json.optLong("department", -1);
        color = json.optString("color");
    }

    public String getShortName() {
        String subShortName = short_name;

        // Create short name if doesn't already exist
        if(subShortName.length() == 0){
            String[] subWords = name.split(" ");
            for (String subWord : subWords) {
                subShortName += subWord.toUpperCase().charAt(0);
            }
        }
        return subShortName;
    }
}
