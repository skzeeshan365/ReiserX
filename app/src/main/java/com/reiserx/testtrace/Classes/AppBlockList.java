package com.reiserx.testtrace.Classes;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.testtrace.Models.AppListInfo;
import com.reiserx.testtrace.Utilities.CONSTANTS;
import com.reiserx.testtrace.Utilities.DataStoreHelper;

import java.util.ArrayList;
import java.util.List;

public class AppBlockList {
    Context context;
    String UserID;
    DataStoreHelper dataStoreHelper;

    public AppBlockList(Context context, String UserID) {
        this.context = context;
        this.UserID = UserID;
        blockUninstall();
        dataStoreHelper = new DataStoreHelper();
    }

    public void update() {
        List<AppListInfo> list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("DisabledApps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        AppListInfo appListInfo = snapshot1.getValue(AppListInfo.class);
                        list.add(appListInfo);
                    }
                    appList(list, UserID);
                } else {
                    appList(list, UserID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    void appList(List<AppListInfo> list, String UserID) {
        try {
            final PackageManager pm = context.getPackageManager();
            List<ApplicationInfo> apps = pm.getInstalledApplications(0);
            for (ApplicationInfo app : apps) {
                if (!list.isEmpty()) {
                    for (AppListInfo appListInfo : list) {
                        if (app.packageName.equals(appListInfo.getPackageName())) {
                            dataStoreHelper.putBooleanValue(app.packageName, true);
                        } else {
                            dataStoreHelper.removeValue(app.packageName);
                        }
                    }
                } else
                    dataStoreHelper.removeValue(app.packageName);
            }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }

    void blockUninstall() {
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("ServiceStatus").child("canUninstall").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean canUninstall = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                    dataStoreHelper.putBooleanValue(CONSTANTS.BLOCK_UNINSTALL, canUninstall);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
