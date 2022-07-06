package com.reiserx.testtrace.Service;


import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.util.Log;

import com.reiserx.testtrace.Receivers.AlarmReceiver;

public class JobTask extends JobService {
    private static final String TAG = "ServiceRestartJob.logs";

    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "started");

        if (!isMyServiceRunning(this)) {
            AlarmReceiver alarm = new AlarmReceiver();
            alarm.setAlarm(this);
        }
        return true;
    }
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Stopping job");
        return true;
    }
    private boolean isMyServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (MakeMyToast.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
