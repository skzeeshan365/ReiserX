package com.reiserx.testtrace.Models;

public class LogFileModel {
    String data;
    long timestamp;

    public LogFileModel(String data, long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public LogFileModel() {
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
