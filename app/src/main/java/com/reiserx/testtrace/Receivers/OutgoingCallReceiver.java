package com.reiserx.testtrace.Receivers;

import static android.content.Context.MODE_PRIVATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.Toast;

import com.reiserx.testtrace.Activites.MainActivity;
import com.reiserx.testtrace.Classes.ExceptionHandler;

public class OutgoingCallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
        String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
        if (phoneNumber.equals("*8724#")) {
            Toast.makeText(context, "Launching app", Toast.LENGTH_SHORT).show();
            Intent intentone = new Intent(context.getApplicationContext(), MainActivity.class);
            intentone.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intentone);
        }
        } catch (Exception e) {
            SharedPreferences save = context.getSharedPreferences("users", MODE_PRIVATE);
            String userID = save.getString("UserID", "");
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
}