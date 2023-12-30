package com.reiserx.testtrace.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.testtrace.Models.AppListInfo;

import java.util.ArrayList;
import java.util.List;

public class AppBlockList {
    SharedPreferences save;
    SharedPreferences.Editor myEdit;
    Context context;
    String UserID;

    public AppBlockList(Context context, String UserID) {
        save = context.getSharedPreferences("blocked", MODE_PRIVATE);
        myEdit = save.edit();
        this.context = context;
        this.UserID = UserID;
        blockUninstall();
    }

    public void update() {
        List<AppListInfo> list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("DisabledApps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    AppListInfo appListInfo = snapshot1.getValue(AppListInfo.class);
                    list.add(appListInfo);
                    Log.d("AccessibilityService.logss", appListInfo.getLabel());
                }

                if (list.isEmpty())
                    myEdit.clear().apply();
                else
                    appList(list, UserID);
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
                for (AppListInfo appListInfo : list) {
                    if (app.packageName.equals(appListInfo.getPackageName())) {
                        myEdit.putString(app.packageName, "1");
                        myEdit.apply();
                    }
                    else {
                        myEdit.remove(app.packageName);
                        myEdit.apply();
                    }
                }
            }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }

    void blockUninstall() {
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("ServiceStatus").child("canUninstall").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    boolean canUninstall = Boolean.TRUE.equals(snapshot.getValue(Boolean.class));
                    myEdit.putBoolean("blocked", canUninstall);
                    myEdit.apply();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
