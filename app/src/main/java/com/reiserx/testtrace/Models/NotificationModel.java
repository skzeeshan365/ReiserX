package com.reiserx.testtrace.Models;

public class NotificationModel {
    private String Package, Ticker, Title, Text;
    long timestamp;

    public NotificationModel() {
    }

    public NotificationModel(String packages, String ticker, String title, String text, long timestamps) {
        Package = packages;
        Ticker = ticker;
        Title = title;
        Text = text;
        timestamp = timestamps;
    }

    public String getPackage() {
        return Package;
    }

    public void setPackage(String packages) {
        Package = packages;
    }

    public String getTicker() {
        return Ticker;
    }

    public void setTicker(String ticker) {
        Ticker = ticker;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}