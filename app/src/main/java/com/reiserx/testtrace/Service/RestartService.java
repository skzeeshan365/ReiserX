package com.reiserx.testtrace.Service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.reiserx.testtrace.Activites.MainActivity;
import com.reiserx.testtrace.Classes.StartMainService;
import com.reiserx.testtrace.R;

public class RestartService extends Service {

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // create notification
        sendNotification(this, "Service restart","restarting", 2354);
        // start service
        StartMainService StartMainService = new StartMainService();
        StartMainService.startservice(RestartService.this);
        stopSelf();
        return START_NOT_STICKY;
    }

    public void sendNotification(Context context, String title, String content, int id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "ServiceRestart365";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Service restart", NotificationManager.IMPORTANCE_NONE);
            notificationChannel.setDescription("service");

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(context, channel_id);
        notify_bulder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setContentText(content)
                .setContentTitle(title)
                .setContentInfo("info");
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
        }

        notify_bulder.setContentIntent(contentIntent);


        // Gets an instance of the NotificationManager service
        notificationManager.notify(id, notify_bulder.build());
// Notification ID cannot be 0.
        startForeground(id, notify_bulder.getNotification());
    }
}