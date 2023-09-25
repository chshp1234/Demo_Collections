package com.example.aidltest.job.exception;


import com.example.aidltest.job.JobResultBean;

public class UserStopTaskException extends InterruptedException {
    private int exceptionCode;
    private JobResultBean resultBean;

    public UserStopTaskException(){

    }

    public UserStopTaskException(JobResultBean bean, int exceptionCode) {
        super();
        this.exceptionCode = exceptionCode;
        resultBean = bean;
    }

    public UserStopTaskException(JobResultBean bean, String message) {
        super(message);
        resultBean = bean;
    }

    public JobResultBean getResultBean() {
        return resultBean;
    }

    public void setResultBean(JobResultBean resultBean) {
        this.resultBean = resultBean;
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(int exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
