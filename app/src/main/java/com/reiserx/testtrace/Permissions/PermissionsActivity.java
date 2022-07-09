package com.reiserx.testtrace.Permissions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.testtrace.databinding.ActivityPermissionsBinding;

import java.util.ArrayList;

public class PermissionsActivity extends AppCompatActivity {

    ActivityPermissionsBinding binding;

    ArrayList<PermissionsModel> data;
    PermissionsAdapter adapter;

    PermissionsModel permissionsModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermissionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PermissionsAdapter(this, data, PermissionsActivity.this);
        binding.rec.setAdapter(adapter);

        permissionsModel = new PermissionsModel(true, "PERMISSIONS");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("STORAGE", "Used for files and media operations", 1, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("READ_CONTACTS", "uhdshfyah", 2, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("READ_CALL_LOG", "uhdshfyah", 3, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("RECORD_AUDIO", "uhdshfyah", 4, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("CAMERA", "uhdshfyah", 5, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("LOCATION", "uhdshfyah", 6, false);
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel(true, "SPECIAL ACCESS");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("DRAW_OVER_OTHER_APPS", "uhdshfyah", 7, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("USAGE_STATS_ACCESS", "uhdshfyah", 8, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("NOTIFICATION_ACCESS", "uhdshfyah", 9, false);
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel(true, "SERVICES");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("ACCESSIBILITY_SERVICE", "uhdshfyah", 10, false);
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel(true, "BATTERY");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("IGNORE_BATTERY_OPTIMIZATION", "uhdshfyah", 11, false);
        data.add(permissionsModel);

        adapter.notifyDataSetChanged();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 2296:
                adapter.notifyDataSetChanged();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 100: {
                adapter.notifyDataSetChanged();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}