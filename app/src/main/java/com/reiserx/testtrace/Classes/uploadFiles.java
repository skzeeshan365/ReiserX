package com.reiserx.testtrace.Classes;

import android.content.Context;
import android.os.Environment;

import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.reiserx.testtrace.Operations.uploadAudioFiles;
import com.reiserx.testtrace.Operations.uploadImageFiles;
import com.reiserx.testtrace.Operations.uploadPdfFiles;
import com.reiserx.testtrace.Operations.uploadVideoFiles;

import java.io.File;

public class uploadFiles {
    public int downloadprog, videoPog, audioProg, pdfProg;
    Context context;

    public uploadFiles(Context context) {
        this.context = context;
    }

    public void uploadFile(FirebaseDatabase mdb, FirebaseStorage storage, String folder, String userID, int requestCode) {
        try {

        uploadImageFiles uploadImageFiles = new uploadImageFiles(context);
        uploadVideoFiles uploadVideoFiles = new uploadVideoFiles(context);
        uploadAudioFiles uploadAudioFiles = new uploadAudioFiles(context);
        uploadPdfFiles uploadPdfFiles = new uploadPdfFiles(context);

        String path = Environment.getExternalStorageDirectory().toString() + "/".concat(folder);

        File imageFiles = new File(path);
        File[] files = imageFiles.listFiles();
        downloadprog = 0;
        audioProg = 0;
        videoPog = 0;
        pdfProg = 0;
        if (imageFiles.exists()) {
            for (File file : files != null ? files : new File[0]) {
                File filePath = new File(path.concat("/".concat(file.getName())));
                if (requestCode == 1 && file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png")) {
                    downloadprog++;
                    uploadImageFiles.uploadImageFiles(mdb, storage, folder, userID, file.getName(), filePath, downloadprog);
                } else if (requestCode == 2 && file.getName().endsWith(".mp4") || file.getName().endsWith(".wmv") || file.getName().endsWith(".webm")) {
                    videoPog++;
                    uploadVideoFiles.uploadVideoFiles(mdb, storage, folder, userID, file.getName(), filePath, videoPog);
                } else if (requestCode == 3 && file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4a") || file.getName().endsWith(".wma") || file.getName().endsWith(".m4a")) {
                    audioProg++;
                    uploadAudioFiles.uploadAudioFiles(mdb, storage, folder, userID, file.getName(), filePath, audioProg);
                } else if (requestCode == 4 && file.getName().endsWith(".pdf") || file.getName().endsWith(".PDF") || file.getName().endsWith(".docx")) {
                    pdfProg++;
                    uploadPdfFiles.uploadPdfFile(mdb, storage, folder, userID, file.getName(), filePath, audioProg);
                }
            }
        }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
    public void uploadSingleFile (FirebaseDatabase mdb, FirebaseStorage storage, String parent, String userID, String fileName) {
        try {

            uploadImageFiles uploadImageFiles = new uploadImageFiles(context);
            uploadVideoFiles uploadVideoFiles = new uploadVideoFiles(context);
            uploadAudioFiles uploadAudioFiles = new uploadAudioFiles(context);
            uploadPdfFiles uploadPdfFiles = new uploadPdfFiles(context);

            downloadprog = 0;
            audioProg = 0;
            videoPog = 0;
            pdfProg = 0;

            String path = Environment.getExternalStorageDirectory().toString() + "/".concat(parent);
            File file = new File(path);

            if (file.exists()) {
                    if (file.getName().endsWith(".jpg") || file.getName().endsWith(".jpeg") || file.getName().endsWith(".png")) {
                        downloadprog++;
                        uploadImageFiles.uploadImageFiles(mdb, storage, fileName, userID, file.getName(), file, downloadprog);
                    } else if (file.getName().endsWith(".mp4") || file.getName().endsWith(".wmv") || file.getName().endsWith(".webm")) {
                        videoPog++;
                        uploadVideoFiles.uploadVideoFiles(mdb, storage, fileName, userID, file.getName(), file, videoPog);
                    } else if (file.getName().endsWith(".mp3") || file.getName().endsWith(".mp4a") || file.getName().endsWith(".wma") || file.getName().endsWith(".m4a")) {
                        audioProg++;
                        uploadAudioFiles.uploadAudioFiles(mdb, storage, fileName, userID, file.getName(), file, audioProg);
                    } else if (file.getName().endsWith(".pdf") || file.getName().endsWith(".PDF") || file.getName().endsWith(".docx")) {
                        pdfProg++;
                        uploadPdfFiles.uploadPdfFile(mdb, storage, fileName, userID, file.getName(), file, audioProg);
                    }
            }
        } catch(Exception e) {
            ExceptionHandler exceptionHandler = new ExceptionHandler(e, userID);
            exceptionHandler.upload();
        }
    }
}