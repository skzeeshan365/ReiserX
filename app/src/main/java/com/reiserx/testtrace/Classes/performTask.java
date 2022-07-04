package com.reiserx.testtrace.Classes;

import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.testtrace.Models.Task;

public class performTask {
    String path, subPath;
    int requestCode;
    String UserID;
    Task task;

    public performTask(String path, int requestCode, String userID) {
        this.path = path;
        this.requestCode = requestCode;
        UserID = userID;
    }

    public void Task() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (subPath!=null) {
            task = new Task(path, subPath, requestCode);
        } else {
            task = new Task(path, requestCode);
        }
        database.getReference("Main").child(UserID).child("Task")
                .setValue(task)
                .addOnSuccessListener(unused -> {
                });
    }
}
