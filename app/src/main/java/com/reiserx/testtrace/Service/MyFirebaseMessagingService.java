package com.reiserx.testtrace.Service;

import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.LocationFiles.GoogleService;
import com.reiserx.testtrace.Receivers.AlarmReceiver;
import com.reiserx.testtrace.Utilities.JobUtil;
import com.reiserx.testtrace.Utilities.NotificationUtils;

import java.util.Map;
import java.util.Objects;

public class MyFirebaseMessagingService extends FirebaseMessagingService {



    NotificationUtils notificationUtils;

    String TAG = "FCMMessage";

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);

    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        remoteMessage.getData();
        SharedPreferences save = getSharedPreferences("users",MODE_PRIVATE);
        String userID = save.getString("UserID", "");

        try {

        Map<String, String> data = remoteMessage.getData();
        String title = data.get("title");
        String content = data.get("content");
        String id = data.get("id");
            Log.d(TAG, title);
            Log.d(TAG, data.get("requestCode"));
        switch (Integer.parseInt(Objects.requireNonNull(data.get("requestCode")))) {
            case 7:
                notificationUtils = new NotificationUtils();
                notificationUtils.silentNotification(this, title, content, Integer.parseInt(Objects.requireNonNull(id)));
                break;
            case 6:
                notificationUtils = new NotificationUtils();
                if (id != null) {
                    notificationUtils.startAppNotification(this, "Launch app", "Request code ".concat(id), Integer.parseInt(id));
                }
                break;
            case 5:
                AlarmReceiver alarm = new AlarmReceiver();
                alarm.setAlarm(this);
                break;
            case 4:
                Intent loc = new Intent(this, GoogleService.class);
                startService(loc);
                break;
            case 3:
                JobUtil.scheduleJob(this);
                break;
            case 2:
                alarm = new AlarmReceiver();
                alarm.setAlarm(this);
                notificationUtils = new NotificationUtils();
                notificationUtils.sendNotification(this, title, content, Integer.parseInt(Objects.requireNonNull(id)));
                break;
            case 1:
                Intent in = new Intent(this, RestartService.class);
                startService(in);
                break;
            case 0:
                alarm = new AlarmReceiver();
                alarm.setAlarm(this);
            break;

        }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
}
