package com.reiserx.testtrace.Classes;


import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.testtrace.Models.exceptionUpload;

import java.util.Calendar;

public class ExceptionHandler {
    Exception e;
    String UserID;

    public ExceptionHandler(Exception e, String UserID) {
        this.e = e;
        this.UserID = UserID;
    }
    public void upload() {
        StackTraceElement exception = e.getStackTrace()[0];
        String className = exception.getClassName();
        String methodName = exception.getMethodName();
        String filename = exception.getFileName();
        int LineNumber = exception.getLineNumber();
        String stackTrace = e.toString();
        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();

        FirebaseDatabase mdb = FirebaseDatabase.getInstance();
        String clase = className.replace(".", "");
        DatabaseReference ref = mdb.getReference("Main").child(UserID).child("Errors").child((clase+LineNumber));
        exceptionUpload exceptionUpload = new exceptionUpload(className, methodName, e.toString(), filename, stackTrace, LineNumber, currentTime);
        ref.setValue(exceptionUpload);
    }
}
