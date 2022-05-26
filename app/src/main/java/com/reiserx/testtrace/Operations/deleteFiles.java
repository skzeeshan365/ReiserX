package com.reiserx.testtrace.Operations;

import android.os.Environment;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.testtrace.Classes.ExceptionHandler;

import java.io.File;

public class deleteFiles {

    public void deleteFiles(String value, String UserID, FirebaseDatabase mdb) {

        try {

        File imageFiles = new File(Environment.getExternalStorageDirectory().toString() + "/".concat(value));

        if (imageFiles.exists() && imageFiles.canWrite() && imageFiles.canRead()) {
            if (imageFiles.isDirectory()) {
                if (deleteDir(imageFiles)) {
                    mdb.getReference("Main").child(UserID).child("Folders").child("Deleted")
                            .setValue(imageFiles.getName().concat(" has been deleted successfully"))
                            .addOnSuccessListener(unused -> Log.d("DeleteFilesTask", "Deleted Directory: ".concat(imageFiles.getName())));
                }
            } else if (imageFiles.isFile()) {
                if (imageFiles.delete()) {
                    mdb.getReference("Main").child(UserID).child("Folders").child("Deleted")
                            .setValue(imageFiles.getName().concat(" has been deleted successfully"))
                            .addOnSuccessListener(unused -> Log.d("DeleteFilesTask", "Deleted File: ".concat(imageFiles.getName())));
                }
            }
        } else {
            mdb.getReference("Main").child(UserID).child("Folders").child("Deleted")
                    .setValue(imageFiles.getName().concat(" does not exists"))
                    .addOnSuccessListener(unused -> {
                    });
            Log.d("DeleteFilesTask", "not exists");
        }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }

    public static boolean deleteDir(File dir) {
            String[] children = dir.list();
            if (children!=null)
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }

        // The directory is now empty so delete it
        return dir.delete();
    }
}
