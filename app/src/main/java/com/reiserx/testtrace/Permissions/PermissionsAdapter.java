package com.reiserx.testtrace.Permissions;

import static android.Manifest.permission.ACCESS_BACKGROUND_LOCATION;
import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CALL_LOG;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.Context.POWER_SERVICE;
import static android.os.Build.VERSION.SDK_INT;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.reiserx.testtrace.NotificationClasses.NotificationService;
import com.reiserx.testtrace.R;
import com.reiserx.testtrace.databinding.PermissionLayoutMainBinding;
import com.reiserx.testtrace.databinding.PermissionsLayoutHeaderBinding;

import java.util.ArrayList;
import java.util.List;

public class PermissionsAdapter extends RecyclerView.Adapter {

    Context context;
    ArrayList<PermissionsModel> data;

    String TAG = "hfhyuehf";
    String [] permissions;

    final int DATA_CONTENT = 1;
    final int HEADER = 2;

    PermissionsActivity activity;

    public PermissionsAdapter(Context context, ArrayList<PermissionsModel> data, PermissionsActivity activity) {
        this.context = context;
        this.data = data;
        this.activity = activity;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == DATA_CONTENT) {
            View view = LayoutInflater.from(context).inflate(R.layout.permission_layout_main, parent, false);
            return new DataViewHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.permissions_layout_header, parent, false);
            return new HeaderViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        PermissionsModel model = data.get(position);

        if (holder.getClass() == DataViewHolder.class) {
            DataViewHolder viewHolder = (DataViewHolder) holder;
            viewHolder.binding.switch1.setClickable(false);
            viewHolder.binding.switch1.setText(model.getContentName());
            viewHolder.binding.contentDescription.setText(model.getContentDescription());

            viewHolder.binding.getRoot().setOnClickListener(v -> {
            });

            processPermissions(model.PermissionCode, viewHolder.binding.switch1, viewHolder.itemView);
        } else if (holder.getClass() == HeaderViewHolder.class) {
            HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
            viewHolder.binding.textView4.setText(model.getContentName());
        }
    }

    private void processPermissions(int permissionCode, Switch switch1, View itemView) {
        switch (permissionCode) {
            case 1:
                switch1.setChecked(checkStoragePermission());
                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        reqStoragePermission();
                        switch1.setChecked(true);
                    }
                });
                break;
            case 2:
                permissions = new String[]{
                        READ_CONTACTS};
                switch1.setChecked(checkPermissions(permissions));
                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{READ_CONTACTS}, 100);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 3:
                permissions = new String[]{
                        READ_CALL_LOG};
                switch1.setChecked(checkPermissions(permissions));
                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        permissions = new String[]{
                                READ_CALL_LOG};
                        req(permissions);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 4:
                permissions = new String[]{
                        RECORD_AUDIO};
                switch1.setChecked(checkPermissions(permissions));
                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{RECORD_AUDIO}, 100);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 5:
                permissions = new String[]{
                        CAMERA};
                switch1.setChecked(checkPermissions(permissions));

                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        ActivityCompat.requestPermissions(activity, new String[]{CAMERA}, 100);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 6:
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    permissions = new String[]{
                            ACCESS_BACKGROUND_LOCATION};
                } else {
                    permissions = new String[]{
                            ACCESS_COARSE_LOCATION,
                            ACCESS_FINE_LOCATION};
                }
                switch1.setChecked(checkPermissions(permissions));

                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                            permissions = new String[]{
                                    ACCESS_BACKGROUND_LOCATION};
                            AlertDialog.Builder alert = new AlertDialog.Builder(context);
                            alert.setTitle("Background location permission");
                            alert.setMessage("By clicking next you will be redirected to (ReiserX AppInfo) page from there go to permissions and grant background location permission/Access all the time");
                            alert.setPositiveButton("next", (dialogInterface, i) -> {
                                startInstalledAppDetailsActivity(activity);
                            });
                            alert.setCancelable(false);
                            alert.show();
                        } else {
                            ActivityCompat.requestPermissions(activity, new String[]{ACCESS_COARSE_LOCATION, ACCESS_FINE_LOCATION}, 100);
                        }
                        switch1.setChecked(true);
                    }
                });
                break;
            case 7:

                switch1.setChecked(Settings.canDrawOverlays(context));

                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.getPackageName()));
                        activity.startActivityForResult(intent, 2296);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 8:
                switch1.setChecked(isAccessGranted());

                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        Intent usage = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        context.startActivity(usage);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 9:

                ComponentName cn = new ComponentName(context, NotificationService.class);
                String flat = Settings.Secure.getString(context.getContentResolver(), "enabled_notification_listeners");
                final boolean enabled = flat != null && flat.contains(cn.flattenToString());

                switch1.setChecked(enabled);


                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        Intent intents = new Intent(
                                "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                        context.startActivity(intents);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 10:

                switch1.setChecked(checkAccessibilityPermission());
                if (!switch1.isChecked()) {
                    Toast.makeText(context, "please turn on Accessibility Service", Toast.LENGTH_SHORT).show();
                }

                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        // request permission via start activity for result
                        context.startActivity(intent);
                        switch1.setChecked(true);
                    }
                });
                break;
            case 11:
                String packageName = context.getPackageName();
                PowerManager pm = (PowerManager) context.getSystemService(POWER_SERVICE);

                switch1.setChecked(pm.isIgnoringBatteryOptimizations(packageName));

                itemView.setOnClickListener(view -> {
                    if (switch1.isChecked()) {
                        undue();
                    } else {
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                        intent.setData(Uri.parse("package:" + packageName));
                        context.startActivity(intent);
                        switch1.setChecked(true);
                    }
                });
                break;
        }
    }

    public void undue () {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("TURN OF");
        alert.setMessage("To turn of this feature go to settings");
        alert.setPositiveButton("ok", null);
        alert.show();
    }

    private void req(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context.getApplicationContext(), p);
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p);
            }
        }

        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);
        }
    }

    private boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            return Environment.isExternalStorageManager();
        } else
            return ActivityCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkPermissions(String[] permissions) {
        int result;
        List<String> listPermissionsNeeded = new ArrayList<>();

        for (String p : permissions) {
            result = ContextCompat.checkSelfPermission(context.getApplicationContext(), p);
                if (result != PackageManager.PERMISSION_GRANTED) {
                    listPermissionsNeeded.add(p);
                }
        }

        return listPermissionsNeeded.isEmpty();
    }

    private void reqStoragePermission() {
        if (SDK_INT >= Build.VERSION_CODES.R) {
            try {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.addCategory("android.intent.category.DEFAULT");
                intent.setData(Uri.parse(String.format("package:%s", context.getApplicationContext().getPackageName())));
                activity.startActivityForResult(intent, 2296);
            } catch (Exception e) {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, 2296);
            }
        } else {
            //below android 11
            ActivityCompat.requestPermissions(activity, new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 100);
        }
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
        context.startActivityForResult(i, 2296);
    }

    @Override
    public int getItemViewType(int position) {
        PermissionsModel model = data.get(position);
        if (model.isHeader) {
            return HEADER;
        } else {
            return DATA_CONTENT;
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class DataViewHolder extends RecyclerView.ViewHolder {

        PermissionLayoutMainBinding binding;

        public DataViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PermissionLayoutMainBinding.bind(itemView);
        }
    }

    public class HeaderViewHolder extends RecyclerView.ViewHolder {

        PermissionsLayoutHeaderBinding binding;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = PermissionsLayoutHeaderBinding.bind(itemView);
        }
    }
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        if (accessEnabled == 0) {
            Log.d(TAG, "start 1");
            return false;
        } else {
            Log.d(TAG, "start 2");
            return true;
        }
    }
}