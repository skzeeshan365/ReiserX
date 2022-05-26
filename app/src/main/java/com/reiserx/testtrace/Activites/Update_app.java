package com.reiserx.testtrace.Activites;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.BuildConfig;
import com.reiserx.testtrace.Utilities.fileDownloader;
import com.reiserx.testtrace.databinding.ActivityUpdateAppBinding;

public class Update_app extends AppCompatActivity {

    ActivityUpdateAppBinding binding;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateAppBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String version = getIntent().getStringExtra("version");
        binding.version.setText("Version ".concat(version));

        if (version.equals(BuildConfig.VERSION_NAME)) {
            binding.button5.setEnabled(false);
            binding.button5.setText("installed");
        } else {
            binding.button5.setEnabled(true);
            binding.button5.setText("install");
        }

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference reference = storage.getReference().child("App").child("Target").child("app-release.apk");

        binding.button5.setOnClickListener(view -> {
            reference.getDownloadUrl().addOnSuccessListener(uri -> {
                String url = uri.toString();
                fileDownloader asyncTask = new fileDownloader(this);
                asyncTask.execute(url);
            });
        });
    }
}