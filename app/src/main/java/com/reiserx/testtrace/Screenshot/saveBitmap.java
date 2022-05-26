package com.reiserx.testtrace.Screenshot;

import android.content.Context;
import android.graphics.Bitmap;

import com.google.firebase.database.DatabaseReference;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.TaskSuccess;

import java.io.File;
import java.io.FileOutputStream;

public class saveBitmap {
    Bitmap bitmap;
    Context context;
    TaskSuccess taskSuccess;
    DatabaseReference reference;

    public saveBitmap(Bitmap bitmap, Context context, DatabaseReference reference, TaskSuccess taskSuccess) {
        this.bitmap = bitmap;
        this.context = context;
        this.reference = reference;
        this.taskSuccess = taskSuccess;
    }

    public void saveData (String filename, String UserID) {
        File sd = new File(context.getFilesDir() + "ReiserX");
        if (!sd.exists()) {
            sd.mkdir();
        }
        File dest = new File(sd, filename);
        try {
            FileOutputStream out = new FileOutputStream(dest);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            taskSuccess.setMessage("Uploading to server");
            taskSuccess.setSuccess(true);
            taskSuccess.setFinal(false);
            reference.setValue(taskSuccess);
            updateToServer updateToServer = new updateToServer(accessibilityService.instance, UserID, reference, taskSuccess);
            updateToServer.update();
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }
}
