package com.reiserx.testtrace.Activites;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.androidhiddencamera.HiddenCameraFragment;
import com.reiserx.testtrace.R;

public class OverlayActivity extends AppCompatActivity {

    private HiddenCameraFragment mHiddenCameraFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overlay);
    }
    @Override
    public void onBackPressed() {
        if (mHiddenCameraFragment != null) {    //Remove fragment from container if present
            getSupportFragmentManager()
                    .beginTransaction()
                    .remove(mHiddenCameraFragment)
                    .commit();
            mHiddenCameraFragment = null;
        } else { //Kill the activity
            finish();
            super.onBackPressed();
        }
    }
}