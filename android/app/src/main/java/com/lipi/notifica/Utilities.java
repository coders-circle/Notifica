package com.lipi.notifica;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Utilities {
    public static Date getDateTimeFromIso(String dateString) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
            return new Date();
        }
    }

    public static String formatDateTimeToIso(long dateTime) {
        Date date = new Date(dateTime);
        return new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US).format(date) + "Z";
    }

    public static String formatMinutes(int time) {
        int hrs = time / 60;
        int min = time % 60;
        return String.format("%02d:%02d", hrs, min);
    }
}
