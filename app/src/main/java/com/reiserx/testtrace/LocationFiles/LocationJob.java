package com.reiserx.testtrace.LocationFiles;


import android.annotation.SuppressLint;
import android.app.job.JobParameters;
import android.app.job.JobService;
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

import java.util.Calendar;

public class LocationJob extends JobService implements LocationListener {
    private static final String TAG = "JobStarted";

    boolean isGPSEnable = false;
    boolean isNetworkEnable = false;
    double latitude, longitude;
    LocationManager locationManager;
    Location location;
    String userID;


    @SuppressLint("MissingPermission")
    @Override
    public boolean onStartJob(JobParameters params) {
        Log.d(TAG, "started");

        try {
            SharedPreferences save = getSharedPreferences("users", MODE_PRIVATE);
            userID = save.getString("UserID", "");
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        isGPSEnable = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        isNetworkEnable = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (isNetworkEnable) {
            location = null;
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    fn_update(location);
                    locationManager = null;
                    location = null;
                } else LocJobUtil.scheduleJob(this);
            }
        } else if (isGPSEnable) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, this);
            if (locationManager != null) {
                location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (location != null) {
                    latitude = location.getLatitude();
                    longitude = location.getLongitude();
                    fn_update(location);
                    locationManager.removeUpdates(this);
                    locationManager = null;
                    location = null;
                } else LocJobUtil.scheduleJob(this);
            }
        }
        } catch (Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
        return true;
}

    private void fn_update(Location location) {

        Calendar cal = Calendar.getInstance();
        long currentTime = cal.getTimeInMillis();
        com.reiserx.testtrace.Models.locationModel locationModel = new locationModel(location.getLongitude() + "", location.getLatitude() + "", currentTime);
        FirebaseDatabase.getInstance().getReference("Main").child(userID).child("Location")
                .setValue(locationModel);

        updateLocation updateLocation = new updateLocation();
        updateLocation.update(locationModel, userID, this, FirebaseFirestore.getInstance());
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.i(TAG, "Stopping job");
        return true;
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {

    }
}
