package com.lipi.notifica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
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
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

        int currentDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int day = currentDay;

        Period current = Period.get(Period.class, dbHelper, "start_time<=? AND end_time>? AND day=?", new String[]{""+currentTime, ""+currentTime, ""+day}, "start_time");
        Period period = Period.get(Period.class, dbHelper, "start_time>? AND day=?", new String[]{"" + currentTime, "" + day}, "start_time");

        int count = 0;
        while (period==null && count < 7) {
            day = (day+1)%7;
            period = Period.get(Period.class, dbHelper, "day=?", new String[]{""+day}, "start_time");
            count++;
        }

        int remaining;

        if (period != null) {
            remaining = period.start_time - currentTime;
            if (count > 0)
                remaining = 24*60 - currentTime + (count-1) * 24 + period.start_time;

            Subject subject = Subject.get(Subject.class, dbHelper, period.subject);

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

            text += period.getStartTime() + " - " + period.getEndTime() + ")";
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

            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.layout_period_widget);

            // Register an onClickListener to launch MainActivity
            Intent intent1 = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_period_text, pendingIntent);

            updateWidget(context, remoteViews);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
