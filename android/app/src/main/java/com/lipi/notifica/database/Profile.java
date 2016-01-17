package com.lipi.notifica.database;

import android.graphics.Bitmap;
import android.util.Log;

import com.lipi.notifica.Utilities;

import org.json.JSONObject;

import java.util.Calendar;

public class Profile extends Model {
    public String avatar;
    public long downloaded_at;
    public byte[] avatar_data;

    public Profile() {}

    public Profile(JSONObject json) {
        _id = json.optLong("id", -1);
        avatar = json.optString("avatar");
        downloaded_at = Calendar.getInstance().getTimeInMillis();
    }

    public Bitmap getAvatar() {
        return Utilities.getImage(avatar_data);
    }

    public void setAvatar(Bitmap image) {
        if (image != null)
            avatar_data = Utilities.getBytes(image);
    }
}
