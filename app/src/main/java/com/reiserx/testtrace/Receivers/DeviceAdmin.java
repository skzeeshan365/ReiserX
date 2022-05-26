package com.reiserx.testtrace.Receivers;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class DeviceAdmin extends DeviceAdminReceiver {

    String TAG = "MyDeviceAdminRec";
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        Log.d(TAG, "onReceive");
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.e(TAG, "::>>>>2 ");
    }

    @Override
    public CharSequence onDisableRequested(Context context, Intent intent) {
        Log.e(TAG, "::>>>>3 ");

        return "Do not disable this, if you disable the app will not function properly";
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.e(TAG, "::>>>>4 ");
    }
}
