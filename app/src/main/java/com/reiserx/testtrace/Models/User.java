package com.reiserx.testtrace.Models;

public class User {
    public String uid, name, key;
    public Long timestamp;

    public User() {
    }

    public User(String uid, String name, String key, long timestamp) {
        this.uid = uid;
        this.name = name;
        this.key = key;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
