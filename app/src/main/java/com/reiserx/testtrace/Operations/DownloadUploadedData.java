package com.reiserx.testtrace.Operations;

import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.reiserx.testtrace.Classes.downloadFiles;

public class DownloadUploadedData {
    String Path, UserID, dbPath, fileUrl;

    public DownloadUploadedData(String path, String dbPath, String fileUrl, String userID, DatabaseReference reference) {
        Path = path;
        UserID = userID;
        this.dbPath = dbPath;
        this.fileUrl = fileUrl;
        download(reference);
    }

    private void download(DatabaseReference Databasereference) {
        Log.d("hgvhbjhj", Path);
        StorageReference reference = FirebaseStorage.getInstance().getReference().child("Main").child(UserID).child("UploadedFiles").child(Path);
            Log.d("hgvhbjhj", fileUrl);
            downloadFiles downloadFiles = new downloadFiles(Path, dbPath, UserID);
            downloadFiles.download(fileUrl, reference, Databasereference);
    }
}
