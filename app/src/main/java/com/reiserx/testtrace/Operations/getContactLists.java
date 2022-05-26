package com.reiserx.testtrace.Operations;

import static android.content.Context.MODE_PRIVATE;

import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.contacts;

import java.util.ArrayList;
import java.util.HashSet;

public class getContactLists {
    Context context;
    String TAG = "Infossss";

    public getContactLists(Context context) {
        this.context = context;
    }

    public void getContactList(String[] PROJECTION, ArrayList<contacts> contactList, String userID, FirebaseFirestore mdb) {
        Log.d(TAG, "1");
        try {
            ContentResolver cr = context.getContentResolver();

            Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
            if (cursor != null) {
                HashSet<String> mobileNoSet = new HashSet<>();
                try {
                    mdb.clearPersistence();
                    final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                    final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    String name, number;
                    while (cursor.moveToNext()) {
                        name = cursor.getString(nameIndex);
                        number = cursor.getString(numberIndex);
                        number = number.replace(" ", "");
                        if (!mobileNoSet.contains(number)) {
                            contactList.add(new contacts(name, number));
                            mobileNoSet.add(number);
                            SharedPreferences save = context.getSharedPreferences("Contacts", MODE_PRIVATE);
                            SharedPreferences.Editor myEdit = save.edit();

                            if (save.getString(number, "").equals("")) {
                                Log.d(TAG, number);
                                contacts contacts = new contacts(name, number);
                                CollectionReference document = mdb.collection("Main").document(userID).collection("Contacts");
                                document.add(contacts)
                                        .addOnSuccessListener(documentReference -> Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId()))
                                        .addOnFailureListener(e -> Log.w(TAG, "Error adding document", e));
                                myEdit.putString(number, number);
                                myEdit.apply();

                            }
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        } catch(Exception e) {
            Log.d(TAG, e.toString());
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }

}