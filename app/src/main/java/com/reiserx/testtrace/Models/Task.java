package com.reiserx.testtrace.Models;

public class Task {
        public String task, subTask;
        public int requestCode;

        public Task() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public Task(String task, int requestCode) {
            this.task = task;
            this.requestCode = requestCode;
        }

    public Task(String task, String subTask, int requestCode) {
        this.task = task;
        this.subTask = subTask;
        this.requestCode = requestCode;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getCode() {
        return requestCode;
    }

    public void setCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public String getSubTask() {
        return subTask;
    }

    public void setSubTask(String subTask) {
        this.subTask = subTask;
    }
}
