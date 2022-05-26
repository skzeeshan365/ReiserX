package com.reiserx.testtrace.Models;

public class exceptionUpload {
    String className, methodName, message, filename, stackTrace;
    int lineNumber;
    long timestamp;

    public exceptionUpload(String className, String methodName, String message, String filename, int lineNumber, long timestamp) {
        this.className = className;
        this.methodName = methodName;
        this.message = message;
        this.lineNumber = lineNumber;
        this.filename = filename;
        this.timestamp = timestamp;
    }

    public exceptionUpload(String className, String methodName, String message, String filename, String stackTrace, int lineNumber, long timestamp) {
        this.className = className;
        this.methodName = methodName;
        this.message = message;
        this.filename = filename;
        this.stackTrace = stackTrace;
        this.lineNumber = lineNumber;
        this.timestamp = timestamp;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
