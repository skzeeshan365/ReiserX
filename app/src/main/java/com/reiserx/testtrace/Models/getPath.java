package com.reiserx.testtrace.Models;

public class getPath {

        public String path;

        public getPath() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

    public getPath(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
