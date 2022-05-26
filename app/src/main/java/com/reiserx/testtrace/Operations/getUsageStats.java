package com.reiserx.testtrace.Operations;

import android.app.AppOpsManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Models.AppUsageInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class getUsageStats {
    Context context;
    String TAG = "getUsageStatsOfApps";
    String UserID;

    public getUsageStats(Context context, String UserID) {
        this.context = context;
        this.UserID = UserID;
        long hour_in_mil = 24000*60*60; // In Milliseconds
        long end_time = System.currentTimeMillis();
        long start_time = end_time - hour_in_mil;
        if (isAccessGranted()) {
            getUsageStatistics(start_time, end_time);
        }
    }
    void getUsageStatistics(long start_time, long end_time) {
        UsageEvents.Event currentEvent;
        //  List<UsageEvents.Event> allEvents = new ArrayList<>();
        HashMap<String, AppUsageInfo> map = new HashMap<>();
        HashMap<String, List<UsageEvents.Event>> sameEvents = new HashMap<>();

        UsageStatsManager mUsageStatsManager = (UsageStatsManager)
                context.getSystemService(Context.USAGE_STATS_SERVICE);

        if (mUsageStatsManager != null) {
            // Get all apps data from starting time to end time
            UsageEvents usageEvents = mUsageStatsManager.queryEvents(start_time, end_time);

            // Put these data into the map
            while (usageEvents.hasNextEvent()) {
                currentEvent = new UsageEvents.Event();
                usageEvents.getNextEvent(currentEvent);
                if (currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED ||
                        currentEvent.getEventType() == UsageEvents.Event.ACTIVITY_PAUSED) {
                    //  allEvents.add(currentEvent);
                    String key = currentEvent.getPackageName();
                    if (map.get(key) == null) {
                        map.put(key, new AppUsageInfo(key));
                        sameEvents.put(key, new ArrayList<>());
                    }
                    Objects.requireNonNull(sameEvents.get(key)).add(currentEvent);
                }
            }

            // Traverse through each app data which is grouped together and count launch, calculate duration
            for (Map.Entry<String,List<UsageEvents.Event>> entry : sameEvents.entrySet()) {
                int totalEvents = entry.getValue().size();
                if (totalEvents > 1) {
                    for (int i = 0; i < totalEvents - 1; i++) {
                        UsageEvents.Event E0 = entry.getValue().get(i);
                        UsageEvents.Event E1 = entry.getValue().get(i + 1);

                        if (E1.getEventType() == 1 || E0.getEventType() == 1) {
                            Objects.requireNonNull(map.get(E1.getPackageName())).launchCount++;
                        }

                        if (E0.getEventType() == 1 && E1.getEventType() == 2) {
                            long diff = E1.getTimeStamp() - E0.getTimeStamp();
                            Objects.requireNonNull(map.get(E0.getPackageName())).timeInForeground += diff;
                        }
                    }
                }

                // If First eventtype is ACTIVITY_PAUSED then added the difference of start_time and Event occuring time because the application is already running.
                if (entry.getValue().get(0).getEventType() == 2) {
                    long diff = entry.getValue().get(0).getTimeStamp() - start_time;
                    Objects.requireNonNull(map.get(entry.getValue().get(0).getPackageName())).timeInForeground += diff;
                }

                // If Last eventtype is ACTIVITY_RESUMED then added the difference of end_time and Event occuring time because the application is still running .
                if (entry.getValue().get(totalEvents - 1).getEventType() == 1) {
                    long diff = end_time - entry.getValue().get(totalEvents - 1).getTimeStamp();
                    Objects.requireNonNull(map.get(entry.getValue().get(totalEvents - 1).getPackageName())).timeInForeground += diff;
                }
            }

            ArrayList<AppUsageInfo> smallInfoList = new ArrayList<>(map.values());

            // Concatenating data to show in a text view. You may do according to your requirement
            for (AppUsageInfo appUsageInfo : smallInfoList)
            {
                // Do according to your requirement
                if (appUsageInfo!=null) {
                    final PackageManager pm = context.getPackageManager();
                    try {
                        ApplicationInfo app = new ApplicationInfo(pm.getApplicationInfo(appUsageInfo.packageName, PackageManager.GET_META_DATA));
                        if (isUserApp(app)) {
                            appUsageInfo.setAppName(String.valueOf(pm.getApplicationLabel(app)));
                            updateData(appUsageInfo);
                            Log.d(TAG, appUsageInfo.appName+": "+convertSecondsToHMmSs(appUsageInfo.timeInForeground));
                        }
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }

        }

    }
    public static String convertSecondsToHMmSs(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60 ;
        int minutes = (int) ((milliseconds / (1000*60)) % 60);
        int hours   = (int) ((milliseconds / (1000*60*60)) % 24);
        return hours+":"+minutes+":"+seconds;
    }boolean isUserApp(ApplicationInfo ai) {
        int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
        return (ai.flags & mask) == 0;
    }

    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }
    private void updateData (AppUsageInfo appUsageInfo) {
        DocumentReference documents = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("UsageStats").document(appUsageInfo.packageName);
        documents.set(appUsageInfo);
    }
}
