package com.reiserx.testtrace.Screenshot;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.TaskSuccess;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

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

    public void saveData(String filename, String UserID) {
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
            updateToServer updateToServer = new updateToServer(context, UserID, reference, taskSuccess);
            updateToServer.update();
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }

    public void saveDataLocal() {
        // Use MediaStore for Android 10 and above
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Screenshots");
        values.put(MediaStore.Images.Media.IS_PENDING, 1);

        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        try {
            OutputStream out = context.getContentResolver().openOutputStream(uri);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            context.getContentResolver().update(uri, values, null, null);

            Toast.makeText(context, "Screenshot saved in DCIM/Screenshots", Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, context);
            exceptionHandler.upload();
        }
    }
}
