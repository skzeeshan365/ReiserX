package com.reiserx.testtrace.Models;

public class folderInfo {

    public int image, folder, video, audio, pdf;
    public String size;

    public folderInfo() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public folderInfo(int image, int folder, int video, int audio, int pdf, String size) {
        this.image = image;
        this.folder = folder;
        this.video = video;
        this.audio = audio;
        this.size = size;
        this.pdf = pdf;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public int getFolder() {
        return folder;
    }

    public void setFolder(int folder) {
        this.folder = folder;
    }

    public int getVideo() {
        return video;
    }

    public void setVideo(int video) {
        this.video = video;
    }

    public int getAudio() {
        return audio;
    }

    public void setAudio(int audio) {
        this.audio = audio;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getPdf() {
        return pdf;
    }

    public void setPdf(int pdf) {
        this.pdf = pdf;
    }
}
