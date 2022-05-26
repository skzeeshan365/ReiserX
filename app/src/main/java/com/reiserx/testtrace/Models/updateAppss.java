package com.reiserx.testtrace.Models;

public class updateAppss {

    public String version;

    public updateAppss() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public updateAppss(String version) {
        this.version = version;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
