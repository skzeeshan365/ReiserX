package com.reiserx.testtrace.LocationFiles;

import static android.content.Context.LOCATION_SERVICE;
import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.locationModel;
import com.reiserx.testtrace.NotificationClasses.deleteNotification;

import java.util.Calendar;

public class periodicLocation implements LocationListener {
    SharedPreferences save;
    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    LocationManager locationManager;
    Location location;

    String TAG = "periodicLocation";

    Context context;

    FirebaseFirestore firestore;
    String userID;

    public periodicLocation(Context context, FirebaseFirestore firestore, String UserID) {
        this.context = context;
        this.firestore = firestore;
        this.userID = UserID;
    }

    @SuppressLint("MissingPermission")
    public void periodice () {
        try {
        save = context.getSharedPreferences("loc", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = save.edit();
        Calendar cal = Calendar.getInstance();
        int time = cal.get(Calendar.HOUR);

        if (time==0) {
            myEdit.putInt("hr", time);
            myEdit.apply();
            Log.e(TAG, "time 0");
        }

        if (time>save.getInt("hr", 0)) {
            locationManager = (LocationManager) context.getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (isNetworkEnable) {
                location = null;
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        fn_update(location, time);
                        locationManager = null;
                        location = null;
                    }
                }
            } else if (isGPSEnable) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
                if (locationManager != null) {
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        fn_update(location, time);
                        locationManager.removeUpdates(this);
                        locationManager = null;
                        location = null;
                    }
                }
            }
            deleteNotification deleteNotification = new deleteNotification(userID);
            deleteNotification.delete();
        }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
    private void fn_update(Location location, int hour) {

        SharedPreferences periodic = context.getSharedPreferences("loc", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = periodic.edit();
        myEdit.putInt("hr", hour);
        myEdit.apply();

        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();

        com.reiserx.testtrace.Models.locationModel locationModel = new locationModel(location.getLongitude() + "", location.getLatitude() + "", currentTime);
        FirebaseDatabase.getInstance().getReference("Main").child(userID).child("Location")
                .setValue(locationModel);

        updateLocation updateLocation = new updateLocation();
        updateLocation.update(locationModel, userID, context, firestore);
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
