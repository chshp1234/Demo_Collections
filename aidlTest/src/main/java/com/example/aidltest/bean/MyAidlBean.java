package com.example.aidltest.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class MyAidlBean implements Parcelable {

    public int code;
    public String message;

    public MyAidlBean() {}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.code);
        dest.writeString(this.message);
    }

    protected MyAidlBean(Parcel in) {
        this.code = in.readInt();
        this.message = in.readString();
    }

    public void readFromParcel(Parcel _reply){
        this.code = _reply.readInt();
        this.message = _reply.readString();
    }

    public static final Creator<MyAidlBean> CREATOR =
            new Creator<MyAidlBean>() {
                @Override
                public MyAidlBean createFromParcel(Parcel source) {
                    return new MyAidlBean(source);
                }

                @Override
                public MyAidlBean[] newArray(int size) {
                    return new MyAidlBean[size];
                }
            };

    @Override
    public String toString() {
        return "MyAidlBean{" + "code=" + code + ", message='" + message + '\'' + '}';
    }
}
