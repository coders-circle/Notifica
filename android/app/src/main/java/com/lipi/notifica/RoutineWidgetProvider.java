package com.lipi.notifica;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import com.lipi.notifica.database.DbHelper;
import com.lipi.notifica.database.Period;
import com.lipi.notifica.database.Subject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by fhx on 1/7/16.
 */
public class RoutineWidgetProvider extends AppWidgetProvider{
    public String getSubjectName(DbHelper helper, long id){
        Subject subject = Subject.get(Subject.class, helper, id);
        return subject.name;
    }




    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int numWidgets = appWidgetIds.length;
        List<Period> todaysRoutine = new ArrayList<>();
        DbHelper helper = new DbHelper(context);
        todaysRoutine = Period.query(Period.class, helper, "day=?",
                new String[]{(Calendar.getInstance().get(Calendar.DAY_OF_WEEK) - 1) + ""}, null, null, "start_time");
        String widgetText = "";
        for(int i = 0; i < todaysRoutine.size(); i++){
            Period p = todaysRoutine.get(i);
            widgetText += p.getEndTime() + " - " + p.getEndTime() + " : " + getSubjectName(helper, p.subject);
            if( i != todaysRoutine.size()-1 ){
                widgetText += "\r\n";
            }
        }
        // Perform this loop procedure for each App Widget that belongs to this provider
        for (int i = 0; i < numWidgets; i++) {
            int appWidgetId = appWidgetIds[i];
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.layout_routine_widget);
            views.setTextViewText(R.id.subject, widgetText);
            //views.setTextViewText("Hello World");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }
    }
}
