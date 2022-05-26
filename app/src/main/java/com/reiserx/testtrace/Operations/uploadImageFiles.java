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
import java.util.Calendar;

public class uploadImageFiles {
    Context context;

    public uploadImageFiles(Context context) {
        this.context = context;
    }

    public void uploadImageFiles(FirebaseDatabase mdb, FirebaseStorage storage, String folder, String userID, String fileName, File filePath, int downloadprog) {
        try {
        SharedPreferences save = context.getSharedPreferences("uploadFiles", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        String sharedPath = String.valueOf(filePath).replace("/", "");

        if (save.getString(sharedPath+fileName, "").equals("")) {
            Calendar cal = Calendar.getInstance();
            long currentTime = cal.getTimeInMillis();
            StorageReference reference = storage.getReference("Main").child(userID).child(folder).child("Images").child(String.valueOf(currentTime));
            reference.putFile(Uri.fromFile(filePath)).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    reference.getDownloadUrl().addOnSuccessListener(uri -> {
                        FileUpload FileUpload = new FileUpload(uri.toString(), fileName, String.valueOf(currentTime));
                        String folders = folder.replace(".", "");
                        mdb.getReference("Main").child(userID + "/Upload/Images").child(folders)
                                .push()
                                .setValue(FileUpload)
                                .addOnSuccessListener(aVoid -> mdb.getReference("Main").child(userID).child("Task")
                                        .removeValue());
                    });
                }
            });
            myEdit.putString(sharedPath+fileName, fileName);
            myEdit.apply();
        }
            mdb.getReference("Main").child(userID).child("Upload").child("Images").child("Download progress")
                    .setValue(downloadprog);
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
}
