package com.reiserx.testtrace.Models;

public class downloadUrl {
    String url;
    long timeStamp;

    public downloadUrl(String url, long timeStamp) {
        this.url = url;
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
