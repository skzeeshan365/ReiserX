package com.reiserx.testtrace.Python;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.testtrace.Utilities.generateKey;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class PythonMain {
    Context context;
    String codeData;
    Python python;
    String file;
    String fileName;
    String data;
    String UserID;

    String TAG = "kifrig";

    public PythonMain(Context context, String codeData, String UserID) {
        this.context = context;
        this.codeData = codeData;
        this.UserID = UserID;

        file = Environment.getExternalStorageDirectory() + "/ReiserX";
        generateKey generateKey = new generateKey();
        fileName = generateKey.randomString(5);
        try {
            FileUtils.fileWrite(file + "/"+fileName+".py", codeData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "ini");
    }

    public String execute() {
        if (!Python.isStarted()) {
            Python.start(new AndroidPlatform(context));
        }

        try {
            python = Python.getInstance();
            python.getModule("sys").get("path").callAttr("append", file);
            PyObject pyObject = python.getModule(fileName);

            data = pyObject.callAttr("main").toString();
            File file1 = new File(file + "/" + fileName + ".py");
            file1.delete();
            new File(file+"/__pycache__").delete();
        } catch (Exception e) {
            FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("Python").child("output").setValue(e.toString());
            File file1 = new File(file + "/" + fileName + ".py");
            file1.delete();
            new File(file+"/__pycache__").delete();
        }
        return data;
    }
}
