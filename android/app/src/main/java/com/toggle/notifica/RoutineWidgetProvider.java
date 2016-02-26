package com.toggle.notifica;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.toggle.notifica.database.DbHelper;
import com.toggle.notifica.database.Period;
import com.toggle.notifica.database.Subject;

import java.util.Calendar;
import java.util.List;

public class RoutineWidgetProvider extends AppWidgetProvider{

    public String getSubjectName(DbHelper helper, long id){
        Subject subject = Subject.get(Subject.class, helper, id);
        return subject.name;
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        List<Period> todaysRoutine;
        DbHelper helper = new DbHelper(context);
        todaysRoutine = Period.query(Period.class, helper, "day=?",
                new String[]{(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) + ""}, null, null, "start_time");
        String widgetText = "";
        for(int i = 0; i < todaysRoutine.size(); i++){
            Period p = todaysRoutine.get(i);
            widgetText += p.getStartTime() + " - " + p.getEndTime() + " : " + getSubjectName(helper, p.subject);
            if( i != todaysRoutine.size()-1 ){
                widgetText += "\r\n";
            }
        }
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int appWidgetId : appWidgetIds) {
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_routine);
            views.setTextViewText(R.id.subject, widgetText);

            // Register an onClickListener to launch MainActivity
            Intent intent1 = new Intent(context, MainActivity.class);
            intent1.putExtra("start_page", R.id.routine);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.widget_routine, pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
