package com.toggle.notifica;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;

public class NotificaGcmListenerService extends GcmListenerService {

    @Override
    public void onMessageReceived(String from, Bundle data) {
        String title = data.getString("title");
        String body = data.getString("body");

        Log.d("GCM", "From: " + from);
        Log.d("GCM", "Message: " + title);
        sendNotification(title, body);
    }

    private void sendNotification(String title, String body) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(getNotificationId(), notificationBuilder.build());

    }

    public int getNotificationId() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        int id = preferences.getInt("notification_id", 0);
        preferences.edit().putInt("notification_id", id+1).apply();
        return id;
    }
}
