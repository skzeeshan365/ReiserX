package com.reiserx.testtrace.Utilities;

import android.os.Environment;

import com.google.gson.Gson;

import org.apache.commons.io.FileUtils;

import java.io.File;

public class addEntryToJsonFile {
    String jsonString;
    File jsonFile;
    String jsonFiles;
    String filename;

    public addEntryToJsonFile(String filename) {
        this.filename = filename;
    }

    public File saveData (Object data) {

        jsonFiles = Environment.getExternalStorageDirectory() + "/.ReiserX/"+filename+".json";
        jsonFile = new File(jsonFiles);

        String previousJson;
        if (jsonFile.exists()) {
            try {
                previousJson = FileUtils.fileRead(jsonFiles);
                if (!previousJson.equals("")) {
                    jsonString = previousJson.concat(",".concat(new Gson().toJson(data)));
                    FileUtils.fileWrite(jsonFiles, jsonString);
                } else {
                    previousJson = new Gson().toJson(data);
                    FileUtils.fileWrite(jsonFiles, previousJson);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            previousJson = new Gson().toJson(data);
            try {
                FileUtils.fileWrite(jsonFiles, previousJson);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return jsonFile;
    }
}
