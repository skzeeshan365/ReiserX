package com.reiserx.testtrace.Models;

public class networkState {

    public boolean state;
    public String timestamp, type;

    public networkState() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public networkState(boolean state, String timestamp, String type) {
        this.state = state;
        this.timestamp = timestamp;
        this.type = type;
    }

    public boolean getState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
