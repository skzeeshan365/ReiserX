package com.reiserx.testtrace.Utilities;


import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;

import com.reiserx.testtrace.Activites.MainActivity;
import com.reiserx.testtrace.BuildConfig;
import com.reiserx.testtrace.R;

public class NotificationUtils {

    public void sendNotification(Context context, String title, String content, int id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "reiserx365";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Cloud messaging", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("service");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 5000, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(context, channel_id);
        notify_bulder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentText(content)
                .setSilent(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content));
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
    }
    public void startAppNotification(Context context, String title, String content, int id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "reiserx365";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Cloud messaging", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("service");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 5000, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(context, channel_id);
        notify_bulder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentText(content)
                .setSilent(true)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content));
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

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            notificationManager.cancelAll();
        }, 10000);
    }
    public void alertWindowPermission(Context context, String title, String content, int id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "reiserx365";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Cloud messaging", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setDescription("service");
            notificationChannel.setVibrationPattern(new long[]{0, 1000, 5000, 1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(context, channel_id);
        notify_bulder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setPriority(NotificationManager.IMPORTANCE_MAX)
                .setContentText(content)
                .setSilent(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content));
        @SuppressLint("UnspecifiedImmutableFlag") PendingIntent contentIntent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);
        } else {
            contentIntent = PendingIntent.getActivity(context, 0,
                    new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + BuildConfig.APPLICATION_ID)), PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notify_bulder.setContentIntent(contentIntent);

        // Gets an instance of the NotificationManager service
        notificationManager.notify(id, notify_bulder.build());
    }

    public void silentNotification(Context context, String title, String content, int id) {

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        String channel_id = "Servicerefresh365";

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(channel_id, "Service refresh", NotificationManager.IMPORTANCE_LOW);
            notificationChannel.setDescription("refresh");
            notificationChannel.enableVibration(false);

            notificationManager.createNotificationChannel(notificationChannel);
        }
        NotificationCompat.Builder notify_bulder = new NotificationCompat.Builder(context, channel_id);
        notify_bulder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_baseline_circle_notifications_24)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .setContentText(content)
                .setSilent(true)
                .setContentTitle(title)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(content));
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
    }
}
