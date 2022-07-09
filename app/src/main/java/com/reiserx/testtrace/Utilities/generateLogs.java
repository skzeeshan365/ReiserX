package com.reiserx.testtrace.Utilities;

import android.annotation.SuppressLint;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Models.LogFileModel;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;

public class generateLogs {

    String TAG = "generateLogs.logs";

    public String writeLogToFile(String lines) {
        StringBuilder logs = new StringBuilder();
        int pid = android.os.Process.myPid();
        @SuppressLint("DefaultLocale") String pidPattern = String.format("%d):", pid);
        try {
            Process process = new ProcessBuilder()
                    .command("logcat", "-t", lines, "-v", "time")
                    .redirectErrorStream(true)
                    .start();

            InputStream in = null;

            try {
                in = process.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(pidPattern)) {
                        logs.append(line).append("\n");
                    }
                }
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        Log.e(TAG, "Cannot close input stream", e);
                    }
                }
            }
        } catch (IOException e) {
            Log.e(TAG, "Cannot read logs", e);
        }

        return logs.toString();
    }
    public void send(String UserID, String lines) {
        String data = writeLogToFile(lines);
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        LogFileModel logFileModel = new LogFileModel(data, currentTime);
        DocumentReference documents = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("Logs").document("LogId");
        documents.set(logFileModel).addOnSuccessListener(aVoid -> Log.d("aSignOfLog", "Test log"))
                .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));
    }
}
