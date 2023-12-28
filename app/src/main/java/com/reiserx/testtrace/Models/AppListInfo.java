package com.reiserx.testtrace.Models;

public class AppListInfo {
    String packageName, Label;
    boolean processStatus;

    public AppListInfo() {
    }

    public AppListInfo(String packageName, String Label) {
        this.packageName = packageName;
        this.Label = Label;
    }

    public AppListInfo(String packageName, String Label, boolean processStatus) {
        this.packageName = packageName;
        this.processStatus = processStatus;
        this.Label = Label;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public boolean isProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(boolean processStatus) {
        this.processStatus = processStatus;
    }

    public String getLabel() {
        return Label;
    }

    public void setLabel(String label) {
        Label = label;
    }
}
