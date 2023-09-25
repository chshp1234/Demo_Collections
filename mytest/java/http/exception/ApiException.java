package com.example.aidltest.http.exception;

/**
 * 自定义异常类
 *
 * @author csp
 * @date 2017/9/22
 */
public class ApiException extends Exception {
    /** 错误码 */
    private int code;
    /** 错误信息 */
    private String msg;

    public ApiException(Throwable throwable, int code) {
        super(throwable);
        this.code = code;
    }

    public ApiException(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
