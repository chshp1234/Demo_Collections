package com.example.aidltest.job;

public class JobResultBean {
    private boolean jobResult;
    private String jobMessage;
    private int jobCode;

    public JobResultBean(boolean jobResult, String message) {
        this.jobResult = jobResult;
        jobMessage = message;
    }

    public JobResultBean(boolean jobResult, int jobCode) {
        this.jobResult = jobResult;
        this.jobCode = jobCode;
    }

    public JobResultBean(boolean jobResult, int jobCode, String message) {
        this.jobResult = jobResult;
        this.jobCode = jobCode;
        this.jobMessage = message;
    }

    public JobResultBean() {
        jobResult = true;
    }

    public boolean isJobResult() {
        return jobResult;
    }

    public void setJobResult(boolean jobResult) {
        this.jobResult = jobResult;
    }

    public String getJobMessage() {
        return jobMessage;
    }

    public void setJobMessage(String jobMessage) {
        this.jobMessage = jobMessage;
    }

    public int getJobCode() {
        return jobCode;
    }

    public void setJobCode(int jobCode) {
        this.jobCode = jobCode;
    }
}
