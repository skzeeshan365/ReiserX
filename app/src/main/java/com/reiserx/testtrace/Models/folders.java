package com.reiserx.testtrace.Models;

public class folders {
    public String folder;

    public folders() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public folders(String folder) {
        this.folder = folder;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }
}
