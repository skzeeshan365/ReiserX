package com.reiserx.testtrace.Models;

public class struct {

        public String bitmap;
        public String name;

        public struct() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public struct(String bitmap, String name) {
            this.bitmap = bitmap;
            this.name = name;
        }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
