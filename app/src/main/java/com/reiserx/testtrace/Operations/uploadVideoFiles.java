package com.reiserx.testtrace.Operations;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.FileUpload;

import java.io.File;

public class uploadVideoFiles {
    Context context;

    public uploadVideoFiles(Context context) {
        this.context = context;
    }

    public void uploadVideoFiles(FirebaseDatabase mdb, FirebaseStorage storage, String folder, String userID, String fileName, File filePath, int prog) {
        try {

        //Upload videos

        SharedPreferences save = context.getSharedPreferences("uploadFiles",MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

            String sharedPath = String.valueOf(filePath).replace("/", "");

        if (save.getString(sharedPath+fileName, "").equals("")) {
            long number = (long) Math.floor(Math.random() * 9_000_000_000L) + 1_000_000_000L;
            StorageReference reference = storage.getReference("Main").child(userID).child(folder).child("Videos").child(String.valueOf(number));
            reference.putFile(Uri.fromFile(filePath))
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            reference.getDownloadUrl().addOnSuccessListener(uri -> {

                                String uris = uri.toString();
                                String values = folder.replace(".", "");
                                FileUpload FileUpload = new FileUpload(uris, fileName);

                                mdb.getReference("Main").child(userID).child("Upload").child("Videos").child(values).child(String.valueOf(number))
                                        .setValue(FileUpload)
                                        .addOnSuccessListener(aVoid -> {
                                        });
                            });

                            mdb.getReference("Main").child(userID).child("Task")
                                    .removeValue()
                                    .addOnSuccessListener(unused -> {

                                    });
                        }
                    });

            myEdit.putString(sharedPath+fileName, fileName);
            myEdit.apply();
        }
        mdb.getReference("Main").child(userID).child("Upload").child("Videos").child("Download progress").setValue(prog);
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }

}
