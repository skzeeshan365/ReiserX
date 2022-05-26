package com.reiserx.testtrace.Models;

public class TaskSuccess {
    String message;
    boolean isSuccess, isFinal;

    public TaskSuccess(String message, boolean isSuccess, boolean isFinal) {
        this.message = message;
        this.isSuccess = isSuccess;
        this.isFinal = isFinal;
    }

    public TaskSuccess() {
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public boolean isFinal() {
        return isFinal;
    }

    public void setFinal(boolean aFinal) {
        isFinal = aFinal;
    }
}
