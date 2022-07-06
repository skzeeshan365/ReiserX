package com.reiserx.testtrace.LocationFiles;

import android.util.Log;

import androidx.annotation.NonNull;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Models.NotificationModel;

import java.util.Objects;

public class deleteLocation {
    String UserID;
    String TAG = "deleteLocation.logs";

    public deleteLocation(String userID) {
        UserID = userID;
    }

    public void deleteData() {
        Log.d(TAG, "start delete");
        FirebaseDatabase.getInstance().getReference().child("Main").child(UserID).child("ServiceStatus").child("AutoDeleteLocation").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int value = snapshot.getValue(int.class);
                    initiateDelete(value);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initiateDelete(int value) {
        CollectionReference documentReference = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("Location");
        documentReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots.getDocuments())) {
                    NotificationModel cn = document.toObject(NotificationModel.class);
                    if (cn != null) {
                        String time = TimeAgo.using(cn.getTimestamp());
                        Log.d(TAG, value+": "+time);
                        switch (value) {
                            case 1:
                                if (time.contains("days")) {
                                    int timeInNumber = Integer.parseInt(TimeAgo.using(cn.getTimestamp()).replaceAll("[\\D]", ""));
                                    if (timeInNumber > 3) {
                                        finishdelete(documentReference, document.getId());
                                        Log.d(TAG, "start delete 1");
                                    }
                                }
                                if (time.contains("months")) {
                                    finishdelete(documentReference, document.getId());
                                }
                                break;
                            case 2:
                                if (time.contains("days")) {
                                    int timeInNumber = Integer.parseInt(TimeAgo.using(cn.getTimestamp()).replaceAll("[\\D]", ""));
                                    if (timeInNumber > 7) {
                                        finishdelete(documentReference, document.getId());
                                        Log.d(TAG, "start delete 2");
                                    }
                                }
                                if (time.contains("months")) {
                                    finishdelete(documentReference, document.getId());
                                }
                                break;
                            case 3:
                                if (time.contains("months")) {
                                    int timeInNumber = Integer.parseInt(TimeAgo.using(cn.getTimestamp()).replaceAll("[\\D]", ""));
                                    if (timeInNumber > 1) {
                                        finishdelete(documentReference, document.getId());
                                        Log.d(TAG, "start delete 3");
                                    }
                                }
                                break;
                            case 4:

                                break;
                        }
                    }
                }
            }
        });
    }

    public void finishdelete(CollectionReference reference, String id) {
        reference.document(id).delete();
    }
}
