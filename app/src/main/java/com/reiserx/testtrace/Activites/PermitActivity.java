package com.reiserx.testtrace.Activites;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Build.VERSION.SDK_INT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.reiserx.testtrace.databinding.ActivityPermitBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PermitActivity extends AppCompatActivity {

    private static final int MULTIPLE_PERMISSIONS = 123;
    ActivityPermitBinding binding;

    String[] permissions;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPermitBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Objects.requireNonNull(getSupportActionBar()).isShowing()) {
            getSupportActionBar().hide();
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            if (checkStoragePermission()) {
                permissions = new String[]{
                        READ_CONTACTS,
                        READ_CALL_LOG, RECORD_AUDIO, CAMERA};
                if (check(permissions)) {
                    binding.button.setText("location");
                } else {
                    binding.button.setText("contacts");
                }
            } else {
                binding.button.setText("storage");
            }
        } else {
            binding.button.setText("grant");
        }

        binding.button.setOnClickListener(v -> {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                if (checkStoragePermission()) {
                    permissions = new String[]{
                            READ_CONTACTS,
                            READ_CALL_LOG, RECORD_AUDIO, CAMERA};
                    if (check(permissions)) {
                        String[] permission = new String[]{
                                ACCESS_BACKGROUND_LOCATION};
                        if (!checkBackLoc(permission)) {
                            startInstalledAppDetailsActivity(PermitActivity.this);
                        }
                    } else {
                        checkPermissions();
                    }
                } else {
                    reqStoragePermission();
                }
            } else {
                permissions = new String[]{
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        READ_CONTACTS,
                        ACCESS_COARSE_LOCATION,
                        ACCESS_FINE_LOCATION,
                        READ_CALL_LOG, RECORD_AUDIO, CAMERA};
                checkPermissions();
            }
        });
    }

    private boolean check(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (result != PackageManager.PERMISSION_GRANTED || !Environment.isExternalStorageManager()) {
                    listPermissionsNeeded.add(p);
                }
            } else {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
            }
        }

        return listPermissionsNeeded.isEmpty();
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else
            return ActivityCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void reqStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(PermitActivity.this, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void checkPermissions() {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : this.permissions) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), MULTIPLE_PERMISSIONS);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MULTIPLE_PERMISSIONS: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {  // permissions granted.
                    if (binding.button.getText().equals("contacts") || binding.button.getText().equals("grant")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                            binding.button.setText("location");
                            binding.button.setOnClickListener(v1 -> {
                                String[] permission = new String[]{
                                        ACCESS_BACKGROUND_LOCATION};
                                if (!checkBackLoc(permission)) {
                                    startInstalledAppDetailsActivity(PermitActivity.this);
                                } else {
                                    checkPerOptimization();
                                }
                            });
                        } else {
                            checkPerOptimization();
                        }
                    }
                } else {
                    String perStr = "";
                    for (String per : permissions) {
                        perStr += "\n" + per;
                    }   // permissions list of don't granted permission
                }
                return;
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 2296:
                if (SDK_INT >= Build.VERSION_CODES.R) {
                    if (Environment.isExternalStorageManager()) {
                        // perform action when allow permission success
                        if (checkStoragePermission()) {
                            binding.button.setText("contacts");
                        } else {
                            binding.button.setText("storage");
                        }
                    }
                }
                break;
            case 365:
                checkPerOptimization();
                break;
            case 35:
                Intent intent1 = new Intent(PermitActivity.this, MainActivity.class);
                startActivity(intent1);
                finish();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @SuppressLint("SetTextI18n")
    public void checkPerOptimization() {
        binding.button.setText("optimization");
        binding.button.setOnClickListener(v -> {
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                binding.button.setOnClickListener(vs -> {
                    intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                    intent.setData(Uri.parse("package:" + packageName));
                    startActivity(intent);
                    Intent intent1 = new Intent(PermitActivity.this, MainActivity.class);
                    startActivity(intent1);
                    finish();
                });
            } else {
                String manufacturer = android.os.Build.MANUFACTURER;
                try {
                    Intent intents = new Intent();
                    if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                        binding.button.setText("AutoStart");
                        binding.button.setOnClickListener(view -> {
                            intents.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
                        });
                    } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                        binding.button.setText("AutoStart");
                        binding.button.setOnClickListener(view -> {
                            intents.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
                        });
                    } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                        binding.button.setText("AutoStart");
                        binding.button.setOnClickListener(view -> {
                            intents.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
                        });
                    } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                        binding.button.setText("AutoStart");
                        binding.button.setOnClickListener(view -> {
                            intents.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
                        });
                    } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                        binding.button.setText("AutoStart");
                        binding.button.setOnClickListener(view -> {
                            intents.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
                        });
                    } else {
                        Intent intent1 = new Intent(PermitActivity.this, MainActivity.class);
                        startActivity(intent1);
                        finish();
                    }

                    List<ResolveInfo> list = getPackageManager().queryIntentActivities(intents, PackageManager.MATCH_DEFAULT_ONLY);
                    if (list.size() > 0) {
                        startActivityForResult(intents, 35);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private boolean checkBackLoc(String[] permiss) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permiss) {
            result = ContextCompat.checkSelfPermission(getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        return listPermissionsNeeded.isEmpty();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivityForResult(i, 365);
    }
}