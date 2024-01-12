package com.reiserx.testtrace.Classes;

import android.app.Application;
import android.content.Context;

import androidx.datastore.preferences.core.Preferences;
import androidx.datastore.preferences.rxjava2.RxPreferenceDataStoreBuilder;
import androidx.datastore.rxjava2.RxDataStore;

import com.reiserx.testtrace.Utilities.DataStore;

public class MyApplication extends Application {
    public static MyApplication instance;
    RxDataStore<Preferences> dataStoreRX;

    String DATA_STORE = "primary";
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        DataStore dataStoreSingleton = DataStore.getInstance();
        if (dataStoreSingleton.getDataStore() == null) {
            dataStoreRX = new RxPreferenceDataStoreBuilder(this, DATA_STORE).build();
        } else {
            dataStoreRX = dataStoreSingleton.getDataStore();
        }
        dataStoreSingleton.setDataStore(dataStoreRX);
    }
    @Override
    public Context getApplicationContext() {
        return super.getApplicationContext();
    }
    public static MyApplication getInstance() {
        return instance;
    }
}
