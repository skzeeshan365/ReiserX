package com.reiserx.testtrace.NotificationClasses;

import androidx.annotation.NonNull;

import com.github.marlonlom.utilities.timeago.TimeAgo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Models.NotificationModel;
import com.reiserx.testtrace.Models.NotificationPath;
import com.reiserx.testtrace.Models.NotificationTitlePath;

import java.util.Objects;

public class deleteNotification {
    String UserID;

    public deleteNotification(String userID) {
        UserID = userID;
    }
    public void delete () {

        FirebaseDatabase mdb = FirebaseDatabase.getInstance();

        mdb.getReference().child("Main").child(UserID).child("Notification history").child("AppName").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                        NotificationPath u = snapshot1.getValue(NotificationPath.class);
                        if (u != null) {
                            String value = u.getName().replace(".", "");
                            String ref = "Main/" + UserID + "/Notification history/Title/" + value;
                            DatabaseReference reference = mdb.getReference().child(ref);
                            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.exists()) {
                                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                                            NotificationTitlePath notificationTitlePath = snapshot1.getValue(NotificationTitlePath.class);
                                            if (notificationTitlePath != null) {
                                                String refdelete = "Main/" + UserID + "/Notification history/Title/" + value + "/" +notificationTitlePath.getName();
                                                initiateDelete(u.getName(), notificationTitlePath.getName(), refdelete);
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void initiateDelete(String pack, String title, String reference) {
        CollectionReference documentReference = FirebaseFirestore.getInstance().collection("Main").document(UserID).collection("Notifications").document(pack).collection(title);
        documentReference.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                for (DocumentSnapshot document : Objects.requireNonNull(queryDocumentSnapshots.getDocuments())) {
                    NotificationModel cn = document.toObject(NotificationModel.class);
                    String time = TimeAgo.using(cn.getTimestamp());
                    if (time.contains("days")) {
                        int timeInNumber = Integer.parseInt(TimeAgo.using(cn.getTimestamp()).replaceAll("[\\D]", ""));
                        if (timeInNumber > 3) {
                            finishdelete(documentReference, document.getId());
                        }
                    }
                    if (queryDocumentSnapshots.size()==0) {
                        FirebaseDatabase.getInstance().getReference().child(reference).removeValue();
                    }
                }
            } else {
                FirebaseDatabase.getInstance().getReference().child(reference).removeValue();
            }
        });
    }
    public void finishdelete (CollectionReference reference, String id) {
        reference.document(id).delete();
    }
}
