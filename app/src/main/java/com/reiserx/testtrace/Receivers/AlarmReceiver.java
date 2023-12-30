package com.reiserx.testtrace.Receivers;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Service.MakeMyToast;

public class AlarmReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
        Intent in = new Intent(context, MakeMyToast.class);
        context.startService(in);
        setAlarm(context);
    }

    public void setAlarm(Context context)
    {
        AlarmManager am =( AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, AlarmReceiver.class);
        PendingIntent pendingIntent = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            pendingIntent = PendingIntent.getBroadcast
                    (context, 0, i, PendingIntent.FLAG_MUTABLE);
        }
        else
        {
            pendingIntent = PendingIntent.getBroadcast
                    (context, 0, i, PendingIntent.FLAG_IMMUTABLE);
        }
        assert am != null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (am.canScheduleExactAlarms())
                am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()/1000L + 15L) *1000L, pendingIntent); //Next alarm in 15s
            else {
                try {
                    throw new Exception("Schedule exact alarm not allowed");
                } catch (Exception e) {
                    ExceptionHandler exceptionHandler = new ExceptionHandler(e, context);
                    exceptionHandler.upload();
                }
            }
        } else
            am.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()/1000L + 15L) *1000L, pendingIntent); //Next alarm in 15s
    }
}