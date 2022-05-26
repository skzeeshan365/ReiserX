package com.reiserx.testtrace.Classes;

import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.reiserx.testtrace.Activites.Update_app;
import com.reiserx.testtrace.BuildConfig;
import com.reiserx.testtrace.Models.updateAppss;

import java.util.Objects;

public class checkUpdate {

    public void check(Context context) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        database.getReference("Administration").child("App").child("Target").child("Updates").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    updateAppss updateAp = snapshot.getValue(updateAppss.class);
                    if (!Objects.requireNonNull(updateAp).version.equals(BuildConfig.VERSION_NAME)) {
                        Intent i = new Intent(context, Update_app.class);
                        i.putExtra("version", updateAp.version);
                        context.startActivity(i);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
