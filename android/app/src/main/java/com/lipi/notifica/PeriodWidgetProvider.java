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

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // Get all ids
        ComponentName thisWidget = new ComponentName(context, PeriodWidgetProvider.class);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);

        for (int widgetId : allWidgetIds) {
            RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_period);

            // Register an onClickListener to launch MainActivity
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            remoteViews.setOnClickPendingIntent(R.id.widget_period_text, pendingIntent);

            updateWidget(context, remoteViews);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }

    public static void updateWidget(Context context, RemoteViews remoteViews) {
        Log.d("updating", "widget");
        // Update widget
        DbHelper dbHelper = new DbHelper(context);
        Calendar cal = Calendar.getInstance();
        int currentTime = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE);

        int currentDay = cal.get(Calendar.DAY_OF_WEEK) - 1;
        int day = currentDay;

        Period period = Period.get(Period.class, dbHelper, "start_time>? AND day=?", new String[]{""+currentTime, ""+day}, "start_time");

        int count = 0;
        while (period==null && count < 7) {
            day = (day+1)%7;
            period = Period.get(Period.class, dbHelper, "day=?", new String[]{""+day}, "start_time");
            count++;
        }

        int remaining = 5;

        if (period != null) {
            remaining = period.start_time - currentTime;
            if (day != currentDay)
                remaining = 24*60 - currentTime + (count-1) * 24 + period.start_time;

            Subject subject = Subject.get(Subject.class, dbHelper, period.subject);
            remoteViews.setTextViewText(R.id.widget_period_text, "Next " + subject.name + " in " + Period.intToTime(remaining));
        }

        /*Intent intent = new Intent(context, PeriodWidgetBroadcastReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        //alarm.cancel(pendingIntent);
        alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + 5000, pendingIntent);*/
    }
}
