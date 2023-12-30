package com.reiserx.testtrace.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AccessibilityFlags {
    Context context;
    SharedPreferences save;
    SharedPreferences.Editor myEdit;
    String UserID;

    public AccessibilityFlags(Context context, String UserID) {
        this.context = context;
        save = context.getSharedPreferences("Flags", MODE_PRIVATE);
        myEdit = save.edit();
        this.context = context;
        this.UserID = UserID;
    }

    public void setFlags() {
        FirebaseDatabase.getInstance().getReference()
                .child("Main")
                .child(UserID)
                .child("ServiceStatus")
                .child("AccessibilityNotification").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Boolean value = snapshot.getValue(Boolean.class);
                        if (Boolean.TRUE.equals(value)) {
                            myEdit.putBoolean("AccessibilityNotify", true);
                            myEdit.apply();
                        } else {
                            myEdit.putBoolean("AccessibilityNotify", false);
                            myEdit.apply();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
