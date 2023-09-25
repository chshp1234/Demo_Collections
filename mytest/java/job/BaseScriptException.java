package com.example.aidltest.job;

public class BaseScriptException extends Exception {
    private int exceptionCode;
    private JobResultBean resultBean;
    private String exceptionMsg;

    public BaseScriptException(JobResultBean bean, int exceptionCode, String exceptionMsg) {
        super();
        this.exceptionCode = exceptionCode;
        resultBean = bean;
        this.exceptionMsg = exceptionMsg;
    }

    public BaseScriptException(JobResultBean bean, String message) {
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

    public String getExceptionMsg() {
        return exceptionMsg;
    }

    public void setExceptionMsg(String exceptionMsg) {
        this.exceptionMsg = exceptionMsg;
    }
}
