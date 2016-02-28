package com.toggle.notifica.database;

import java.util.Calendar;

public class NextPeriodFinder {
    public Period current;
    public Subject currentSubject;
    public Period next;
    public Subject nextSubject;
    public int remaining;   // Minutes
    public String nextDay;

    public NextPeriodFinder(DbHelper dbHelper) {
        Calendar cal = Calendar.getInstance();

        // Get current time and day of week
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Get current and next periods
        current = Period.get(Period.class, dbHelper, "start_time<=? AND end_time>? AND day=?", new String[]{"" + currentTime, "" + currentTime, "" + day}, "start_time");
        next = Period.get(Period.class, dbHelper, "start_time>? AND day=?", new String[]{"" + currentTime, "" + day}, "start_time");

        // If next period isn't today, get tomorrow's period and so on
        int count = 0;
        while (next == null && count < 7) {
            day = (day + 1) % 7;
            next = Period.get(Period.class, dbHelper, "day=?", new String[]{"" + day}, "start_time");
            count++;
        }

        // Current period only if subject also exists
        if (current != null) {
            currentSubject = Subject.get(Subject.class, dbHelper, current.subject);
            if (currentSubject == null)
                current = null;
        }

        remaining = 60;
        if (next != null) {
            // Next period only if subject also exists
            nextSubject = Subject.get(Subject.class, dbHelper, next.subject);
            if (nextSubject == null)
                next = null;
            else {
                // Find remaining time to next period
                remaining = next.start_time - currentTime;
                if (count > 0)
                    remaining = 24 * 60 - currentTime + (count - 1) * 24 + next.start_time;

                if (count == 1)
                    nextDay = "Tomorrow ";
                else if (count > 1) {
                    nextDay = DbHelper.DAYS[day] + " ";
                } else
                    nextDay = "";
            }
        }
    }
}
