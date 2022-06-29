package com.reiserx.testtrace.LocationFiles;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Models.locationModel;

public class updateLocation {

    public void update(locationModel locationModel, String UserID, Context context, FirebaseFirestore firestore) {
        SharedPreferences periodic = context.getSharedPreferences("loc", MODE_PRIVATE);
        SharedPreferences.Editor myEdit = periodic.edit();
        Log.d("LocationsUpdates", "updatelocation");
        String time = TimeAgo.using(periodic.getLong("time", 0));
        if (!time.contains("just now") && !time.contains("one minute ago") && !time.contains("minutes ago")) {
            CollectionReference documents = firestore.collection("Main").document(UserID).collection("Location");
            documents.add(locationModel);
            myEdit.putLong("time", locationModel.getTimestamp());
            myEdit.apply();
            deleteLocation deleteLocation = new deleteLocation(UserID);
            deleteLocation.deleteData();
        }
    }
}
