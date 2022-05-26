package com.reiserx.testtrace.Models;

public class deviceInfo {
    private String serial, model, id, manufacturer, user, versionCode;
    int sdk;

    public deviceInfo(String serial, String model, String id, String manufacturer, String user, int sdk, String versionCode) {
        this.serial = serial;
        this.model = model;
        this.id = id;
        this.manufacturer = manufacturer;
        this.user = user;
        this.sdk = sdk;
        this.versionCode = versionCode;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public int getSdk() {
        return sdk;
    }

    public void setSdk(int sdk) {
        this.sdk = sdk;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }
}
