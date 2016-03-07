package com.toggle.notifica;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.NextPeriodFinder;
import com.toggle.notifica.database.Period;
import com.toggle.notifica.database.Subject;
import com.toggle.notifica.database.User;

import java.util.Calendar;

public class PeriodWidgetProvider extends AppWidgetProvider {

    public static void updateWidget(Context context, RemoteViews remoteViews) {
        User me = User.getLoggedInUser(context);
        if (me == null) {
            remoteViews.setViewVisibility(R.id.now, View.GONE);
            remoteViews.setViewVisibility(R.id.next, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_message, View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_message, "Please log in to view routine");
            return;
        }

        DbHelper dbHelper = new DbHelper(context);

        if (me.getStudent(dbHelper) == null) {
            remoteViews.setViewVisibility(R.id.now, View.GONE);
            remoteViews.setViewVisibility(R.id.next, View.GONE);
            remoteViews.setViewVisibility(R.id.widget_message, View.VISIBLE);
            remoteViews.setTextViewText(R.id.widget_message,
                    "You have to join a class to view routine");
            return;
        }

        remoteViews.setViewVisibility(R.id.widget_message, View.GONE);

        NextPeriodFinder finder = new NextPeriodFinder(dbHelper);
        if (finder.current != null) {
            Subject sub = finder.currentSubject;
            Utilities.fillCurrentPeriod(remoteViews, "Current class",
                    sub.name, finder.current.getPeriodString()
            );
            remoteViews.setViewVisibility(R.id.now, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.now_practical,
                    finder.current.period_type == 1 ? View.VISIBLE : View.GONE);
        } else
            remoteViews.setViewVisibility(R.id.now, View.GONE);

        if (finder.next != null) {
            Subject sub = finder.nextSubject;
            Utilities.fillNextPeriod(remoteViews, "Next class", sub.name,
                    finder.nextDay + finder.next.getPeriodString()
            );
            remoteViews.setViewVisibility(R.id.next, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.next_practical,
                    finder.next.period_type == 1 ? View.VISIBLE : View.GONE);
        } else
            remoteViews.setViewVisibility(R.id.next, View.GONE);


        int nextMinute = finder.remaining*60*1000; //(60-cal.get(Calendar.SECOND))*1000;

        // Set alarm for update in next period
        Intent intent = new Intent(context, PeriodWidgetProvider.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pendingIntent);
        alarm.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime() + nextMinute, pendingIntent);
    }


    @Override
    public void onReceive(Context context, Intent intent)
    {
        super.onReceive(context, intent);
        updateAllWidgets(context);
    }

    public static void updateAllWidgets(final Context context)
    {
        ComponentName thisWidget = new ComponentName(context, PeriodWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] allWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.widget_period);

        for (int widgetId : allWidgetIds) {

            // Register an onClickListener to launch MainActivity or LoginActivity
            Intent intent1;
            if (User.getLoggedInUser(context) == null)
                intent1 = new Intent(context, LoginActivity.class);
            else {
                intent1 = new Intent(context, MainActivity.class);
                intent1.putExtra("start_page", R.id.routine);
            }
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setOnClickPendingIntent(R.id.widget_period, pendingIntent);

            updateWidget(context, remoteViews);

            // Tell the AppWidgetManager to perform an update on the current app widget
            appWidgetManager.updateAppWidget(widgetId, remoteViews);
        }
    }
}
