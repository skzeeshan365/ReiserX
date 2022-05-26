package com.reiserx.testtrace.Models;

public class FileUpload {
    public String url;
    public String name;
    public String id;

    public FileUpload() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public FileUpload(String url, String name) {
        this.url = url;
        this.name = name;
    }

    public FileUpload(String url, String name, String id) {
        this.url = url;
        this.name = name;
        this.id = id;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
