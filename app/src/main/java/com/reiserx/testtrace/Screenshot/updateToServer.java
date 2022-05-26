package com.reiserx.testtrace.Screenshot;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.Models.TaskSuccess;
import com.reiserx.testtrace.Models.downloadUrl;

import java.io.File;
import java.util.Calendar;

public class updateToServer {
    String UserID;
    Context context;

    TaskSuccess taskSuccess;
    DatabaseReference references;

    String TAG = "ijdhfiuhf";

    public updateToServer(Context context, String userID, DatabaseReference reference, TaskSuccess taskSuccess) {
        this.context = context;
        this.UserID = userID;
        this.references = reference;
        this.taskSuccess = taskSuccess;
    }

    public void update () {

        String path = context.getFilesDir() + "ReiserX";
        File directory = new File(path);
        File[] files = directory.listFiles();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        if (files != null && directory.exists()) {
            for (File file : files) {
                if (file.exists()) {
                    File filePath = new File(path.concat("/".concat(file.getName())));
                    Log.d(TAG, file.getName());
                    StorageReference reference = storage.getReference().child("Main").child(UserID).child("ScreenShots").child(file.getName());
                    DocumentReference collectionReference = firestore.collection("Main").document(UserID).collection("ScreenShots").document(file.getName());
                    reference.putFile(Uri.fromFile(filePath)).addOnCompleteListener(task -> {
                        Log.d(TAG, "uploading");
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                                Log.d(TAG, "getting url");
                                Calendar cal = Calendar.getInstance();
                                long currentTime = cal.getTimeInMillis();
                                downloadUrl downloadUrl = new downloadUrl(uri.toString(), currentTime);
                                collectionReference.set(downloadUrl).addOnSuccessListener(reference1 -> {
                                    filePath.delete();
                                    taskSuccess.setMessage("Upload successful");
                                    taskSuccess.setSuccess(true);
                                    taskSuccess.setFinal(true);
                                    references.setValue(taskSuccess);
                                    Log.d(TAG, String.valueOf(reference1));
                                });
                            });
                        }
                    });
                }
            }
        }
    }
}
