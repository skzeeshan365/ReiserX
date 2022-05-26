package com.reiserx.testtrace.NotificationClasses;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.AppListInfo;

import java.util.List;

public class getListOfApps {
    Context context;

    String UserID;

    String TAG = "getListOfAllApps";

    public getListOfApps(Context context, String UserID) {
        this.context = context;
        this.UserID = UserID;
    }

    public void getData (FirebaseFirestore firestore) {
        try {
        final PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for(ApplicationInfo app : apps) {
            DocumentReference documents = firestore.collection("Main").document(UserID).collection("App list").document(app.packageName);
            documents.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        if (document.getData().get("label") == null) {
                            Log.d(TAG, String.valueOf(pm.getApplicationLabel(app)));
                            documents.set(new AppListInfo(app.packageName, String.valueOf(pm.getApplicationLabel(app)), false));
                        }
                    } else {
                        Log.d(TAG, String.valueOf(app.packageName));
                        documents.set(new AppListInfo(app.packageName, String.valueOf(pm.getApplicationLabel(app)), false));
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            });
        }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }
}
