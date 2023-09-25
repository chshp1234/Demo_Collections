package com.example.aidltest.bean;

import com.google.gson.annotations.SerializedName;

public class RequestBean {

    @SerializedName("f")
    private int fNum;

    @SerializedName("c")
    private int cNum;

    @SerializedName("_t")
    private long time;

    @SerializedName("type")
    private int type;

    private String sign;

    public int getfNum() {
        return fNum;
    }

    public void setfNum(int fNum) {
        this.fNum = fNum;
    }

    public int getcNum() {
        return cNum;
    }

    public void setcNum(int cNum) {
        this.cNum = cNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Override
    public String toString() {
        return "RequestBean{"
                + "fNum="
                + fNum
                + ", cNum="
                + cNum
                + ", time="
                + time
                + ", type="
                + type
                + ", sign='"
                + sign
                + '\''
                + '}';
    }
}
