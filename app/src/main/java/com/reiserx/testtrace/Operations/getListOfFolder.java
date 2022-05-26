package com.reiserx.testtrace.Operations;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.folders;

import java.io.File;

public class getListOfFolder {
    Context context;

    public getListOfFolder(Context context) {
        this.context = context;
    }

    public void getListOfFolder(FirebaseDatabase mdb, String userID, String path, String storagePath) {
        try {

        File directory = new File(storagePath);
        File[] Folders = directory.listFiles();

        SharedPreferences save = context.getSharedPreferences("Folders", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        String sharedPath = storagePath.replace("/", "");

        if (Folders != null && directory.exists()) {
            for (File folder : Folders) {
                    if (save.getString(sharedPath+folder.getName(), "").equals("")) {
                        Log.d("iuwhfiwfhiw", folder.getName());
                        folders folders1 = new folders(folder.getName());
                        String value = path.replace(".", "");
                        mdb.getReference("Main").child(userID).child(value)
                                .push()
                                .setValue(folders1);
                        myEdit.putString(sharedPath+folder.getName(), folder.getName());
                        myEdit.apply();
                    }
            }
        }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
}
