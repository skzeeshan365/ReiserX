package com.reiserx.testtrace.Models;

public class locationModel {

    public String longitude, latitude;
    long timestamp;

    public locationModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public locationModel(String longitude, String latitude, long timestamp) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.timestamp = timestamp;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
