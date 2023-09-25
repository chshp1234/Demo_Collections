package com.example.aidltest.base;

/**
 * @author csp
 * @date 2017/9/22
 */
public class BaseBeam<T> {
    int returnValue;
    String errMsg;
    T data;

    public int getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(int returnValue) {
        this.returnValue = returnValue;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "BaseBean{"
                + "returnValue="
                + returnValue
                + ", errMsg='"
                + errMsg
                + '\''
                + ", data="
                + data
                + '}';
    }
}
