package com.reiserx.testtrace.Models;

public class AudiosDownloadUrl {
    String url, name;
    long timeStamp;

    public AudiosDownloadUrl(String url, String name, long timeStamp) {
        this.url = url;
        this.name = name;
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
