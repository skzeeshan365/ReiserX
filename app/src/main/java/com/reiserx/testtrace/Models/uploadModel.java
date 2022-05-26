package com.reiserx.testtrace.Models;

public class uploadModel {
    String path, storagePath, fileUrl;

    public uploadModel(String path, String storagePath, String fileUrl) {
        this.path = path;
        this.storagePath = storagePath;
        this.fileUrl = fileUrl;
    }

    public uploadModel() {
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    public String getFileUrl() {
        return fileUrl;
    }

    public void setFileUrl(String fileUrl) {
        this.fileUrl = fileUrl;
    }
}
