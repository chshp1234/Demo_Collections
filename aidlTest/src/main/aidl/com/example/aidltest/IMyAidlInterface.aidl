// IMyAidlInterface.aidl
package com.example.aidltest;

// Declare any non-default types here with import statements
import com.example.aidltest.bean.MyAidlBean;
import com.example.aidltest.IMyAidlInterfaceReceiver;

interface IMyAidlInterface {
    /**
     * Demonstrates some basic types that you can use as parameters
     * and return values in AIDL.
     */
    oneway void basicTypes(int anInt, long aLong, boolean aBoolean, float aFloat,double aDouble, String aString);

    void sendSingleMessage(in String message);

    void sendMessage(inout MyAidlBean MyAidlBean);

    void registerReceiveListener(IMyAidlInterfaceReceiver messageReceiver);

    void unregisterReceiveListener(IMyAidlInterfaceReceiver messageReceiver);

}
