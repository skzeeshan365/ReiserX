package com.reiserx.testtrace.Operations;

import android.os.Environment;

import com.google.firebase.database.FirebaseDatabase;
import com.reiserx.testtrace.Classes.ExceptionHandler;
import com.reiserx.testtrace.Models.folderInfo;
import com.reiserx.testtrace.Utilities.fileSize;

import java.io.File;

public class checklistOffiles {

    public void checklistOffiles(FirebaseDatabase mdb, String folder, String userID) {

        try {

        String path = Environment.getExternalStorageDirectory().toString() + "/".concat(folder);
        File imagefiles = new File(path);
        File[] files = imagefiles.listFiles();
        int image = 0, Folder = 0, video = 0, audio = 0, pdf = 0;
            fileSize fileSize = new fileSize();

        if (imagefiles.exists()) {
            for (File file : files != null ? files : new File[0]) {
                if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png")) {
                    image++;
                } else if (file.isDirectory()) {
                    Folder++;
                } else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".wmv") || file.getName().endsWith(".webm")) {
                    video++;
                } else if (file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4a") || file.getName().endsWith(".wma") || file.getName().endsWith(".m4a")) {
                    audio++;
                } else if (file.getName().endsWith(".pdf") || file.getName().endsWith(".PDF") || file.getName().endsWith(".docx")) {
                    pdf++;
                }
            }
            folderInfo folderInfo = new folderInfo(image, Folder, video, audio, pdf, fileSize.getFileSize(getFolderSize(imagefiles)));
            String folders = folder.replace(".", "");
            mdb.getReference("Main").child(userID + "/GetFileList/Folder")
                    .child(folders)
                    .setValue(folderInfo)
                    .addOnSuccessListener(aVoid -> mdb.getReference("Main").child(userID).child("Task")
                            .removeValue()
                            .addOnSuccessListener(unused -> {
                            }));
        }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
    public static long getFolderSize(File f) {
        long size = 0;

        if (f.isDirectory()) {
            File[] listFiles = f.listFiles();
            if (listFiles!=null) {
                for (int i = 0, listFilesLength = listFiles.length; i < listFilesLength; i++) {
                    File file = listFiles[i];
                    size += getFolderSize(file);
                }
            }
        } else {
            size=f.length();
        }
        return size;
    }
}
