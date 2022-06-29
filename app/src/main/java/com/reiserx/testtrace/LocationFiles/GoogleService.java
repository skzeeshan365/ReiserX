package com.reiserx.testtrace.LocationFiles;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.locationModel;

import java.util.Calendar;

public class GoogleService extends Service implements LocationListener{

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude,longitude;
    LocationManager locationManager;
    Location location;
    FirebaseDatabase mdb;
    FirebaseFirestore firestore;
    String userID;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("onLocationChanged","runs");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
            userID = save.getString("UserID", "");
            mdb = FirebaseDatabase.getInstance();
            firestore = FirebaseFirestore.getInstance();

            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            fn_getlocation();
            Log.d("LocationsUpdates", "start");
            stopSelf();
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @SuppressLint("MissingPermission")
    private void fn_getlocation(){

        if (isNetworkEnable) {
            Log.d("LocationsUpdates", "network");
            location = null;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
            if (locationManager != null) {
                Log.d("LocationsUpdates", "1");
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    Log.d("LocationsUpdates", "2");
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    fn_update(location);
                    stopSelf();
                    locationManager = null;
                    location = null;
                } else getGpsLOC();
            } else if (isGPSEnable) {
                Log.d("LocationsUpdates", "gps");
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
                if (locationManager != null) {
                    Log.d("LocationsUpdates", "1");
                    location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        Log.d("LocationsUpdates", "2");
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        fn_update(location);
                        stopSelf();
                        locationManager.removeUpdates(this);
                        locationManager = null;
                        location = null;
                    }
                }
            }
        }

    }

    @SuppressLint("MissingPermission")
    public void getGpsLOC () {
        Log.d("LocationsUpdates", "gps");
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
        if (locationManager != null) {
            Log.d("LocationsUpdates", "1");
            location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                Log.d("LocationsUpdates", "2");
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                fn_update(location);
                stopSelf();
                locationManager.removeUpdates(this);
                locationManager = null;
                location = null;
            }
        }
    }


    private void fn_update(Location location){

        deleteLocation deleteLocation = new deleteLocation(userID);
        deleteLocation.deleteData();

        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        locationModel locationModel = new locationModel(location.getLongitude()+"", location.getLatitude()+"", currentTime);
        mdb.getReference("Main").child(userID).child("Location")
                .setValue(locationModel)
                .addOnSuccessListener(unused -> {
                    stopSelf();
                    mdb.getReference("Main").child(userID).child("Task")
                            .removeValue()
                            .addOnSuccessListener(s -> stopSelf());
                });
        Log.d("LocationsUpdates", "getloc");
        Log.d("LocationsUpdates", String.valueOf(locationModel.getTimestamp()));
        updateLocation updateLocation = new updateLocation();
        updateLocation.update(locationModel, userID, this, firestore);

        stopSelf();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        locationManager=null;
        location=null;
    }
}