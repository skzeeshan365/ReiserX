package com.reiserx.testtrace.Models;

public class NotifyVerification {

    public String id, timestamp;

    public NotifyVerification() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public NotifyVerification(String id, String timestamp) {
        this.timestamp = timestamp;
        this.id = id;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
