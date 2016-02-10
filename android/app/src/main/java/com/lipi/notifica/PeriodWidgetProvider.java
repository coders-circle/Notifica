package com.lipi.notifica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.widget.RemoteViews;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Subject;

import java.util.Calendar;

public class PeriodWidgetProvider extends AppWidgetProvider {

    public static void updateWidget(Context context, RemoteViews remoteViews) {
        // Update widget
        DbHelper dbHelper = new DbHelper(context);
        Calendar cal = Calendar.getInstance();

        // Get current time and day of week
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);
        int day = cal.get(Calendar.DAY_OF_WEEK) - 1;

        // Get current next periods
        Period current = Period.get(Period.class, dbHelper, "start_time<=? AND end_time>? AND day=?", new String[]{""+currentTime, ""+currentTime, ""+day}, "start_time");
        Period next = Period.get(Period.class, dbHelper, "start_time>? AND day=?", new String[]{"" + currentTime, "" + day}, "start_time");

        // If next period isn't today, get tomorrow's period and so on
        int count = 0;
        while (next==null && count < 7) {
            day = (day+1)%7;
            next = Period.get(Period.class, dbHelper, "day=?", new String[]{""+day}, "start_time");
            count++;
        }

        int remaining;

        if (next != null) {
            // Find remaining time to next period
            remaining = next.start_time - currentTime;
            if (count > 0)
                remaining = 24*60 - currentTime + (count-1) * 24 + next.start_time;

            Subject subject = Subject.get(Subject.class, dbHelper, next.subject);

            // Show current period if exists
            String text = "";
            if (current != null) {
                Subject sub = Subject.get(Subject.class, dbHelper, current.subject);
                text += sub.name + " " + current.getStartTime() + " - " + current.getEndTime() + "\n";
            }

            // Show next period
            text += "Next " + subject.name + " in " + Utilities.formatMinutes(remaining) + " (";

            if (count == 1)
                text += "Tomorrow ";
            else if (count > 1) {
                text += DbHelper.DAYS[day] + " ";
            }

            text += next.getStartTime() + " - " + next.getEndTime() + ")";
            remoteViews.setTextViewText(R.id.widget_period_text, text);
        }

        // Set alarm for update in next minute
        Intent intent = new Intent(context, PeriodWidgetProvider.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 60000, pendingIntent);
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);

        ComponentName thisWidget = new ComponentName(context, PeriodWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_period);

            // Register an onClickListener to launch MainActivity
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.putExtra("start_page", R.id.routine);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_period, pendingIntent);

            updateWidget(context, remoteViews);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
