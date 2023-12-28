package com.reiserx.testtrace.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;

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

    public AppBlockList(Context context) {
        save = context.getSharedPreferences("blocked", MODE_PRIVATE);
        myEdit = save.edit();
        this.context = context;
    }

    public void update(String UserID) {
        List<AppListInfo> list = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("DisabledApps").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                    AppListInfo appListInfo = snapshot1.getValue(AppListInfo.class);
                    list.add(appListInfo);
                    Toast.makeText(context, appListInfo.getLabel(), Toast.LENGTH_SHORT).show();
                }
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
                    if (app.packageName.equals(appListInfo.getPackageName()))
                        myEdit.putString(app.packageName, "1");
                    else
                        myEdit.remove(app.packageName);
                    myEdit.apply();
                }
            }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, UserID);
            exceptionHandler.upload();
        }
    }
}
