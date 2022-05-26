package com.reiserx.testtrace.Operations;

import android.os.Environment;

import com.reiserx.testtrace.Classes.performTask;

import java.io.File;

public class createFolder {
    String Path, subPath, UserID;

    public createFolder(String path, String subPath, String userID) {
        Path = path;
        this.subPath = subPath;
        UserID = userID;
    }

    public void create () {
        String path = Environment.getExternalStorageDirectory().toString() + "/".concat(Path.concat("/".concat(subPath)));
        File file = new File(path);
        if (file.mkdir()) {
            performTask performTask = new performTask(Path, 1, UserID);
            performTask.Task();
        }
    }
}
