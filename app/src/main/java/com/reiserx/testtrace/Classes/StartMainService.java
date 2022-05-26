package com.reiserx.testtrace.Classes;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.reiserx.testtrace.Receivers.AlarmReceiver;
import com.reiserx.testtrace.Service.MakeMyToast;

import java.util.Objects;

public class StartMainService {

    public void startservice(Context context) {
        try {
            Thread thread = new Thread() {
                public void run() {
                    Looper.prepare();
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (!isMyServiceRunning(context)) {
                                AlarmReceiver alarm = new AlarmReceiver();
                                alarm.setAlarm(Objects.requireNonNull(context));
                            }
                            handler.removeCallbacks(this);
                            Looper.myLooper().quit();
                        }
                    }, 100);

                    Looper.loop();
                }
            };
            thread.start();

        } catch (Exception ex) {
            Log.e("ERROR =>", "" + ex.getMessage());
            ex.printStackTrace();
        }
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