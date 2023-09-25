// IMyAidlInterface.aidl
package com.example.aidltest;

// Declare any non-default types here with import statements

import com.example.aidltest.bean.MyAidlBean;

import com.example.aidltest.bean.MyAidlBean;

 interface IMyAidlInterfaceReceiver {
    void onMessageReceived(in MyAidlBean MyAidlBean);
}
