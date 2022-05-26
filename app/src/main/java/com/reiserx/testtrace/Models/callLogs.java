package com.reiserx.testtrace.Models;

public class callLogs {
    public String phoneNumber, type, duration, dateinsecs;

    public callLogs() {
    }

    public callLogs(String phoneNumber, String type, String duration, String dateinsecs) {
        this.phoneNumber = phoneNumber;
        this.type = type;
        this.duration = duration;
        this.dateinsecs = dateinsecs;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDateinsecs() {
        return dateinsecs;
    }

    public void setDateinsecs(String dateinsecs) {
        this.dateinsecs = dateinsecs;
    }
}
