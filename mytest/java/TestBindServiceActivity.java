package com.example.aidltest;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.base.BaseActivity;
import com.example.aidltest.bean.MyAidlBean;

import java.util.Arrays;

import butterknife.OnClick;

import static com.example.aidltest.MainActivity.startFloatingService;

public class TestBindServiceActivity extends BaseActivity {

    @Override
    protected void initData() {
    }

    @Override
    protected void initView() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                        Log.d(TAG, "run: " + myAidlBean);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }
        }).start();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bind_service;
    }

    MyAidlBean myAidlBean = new MyAidlBean();
    ;

    @OnClick({R.id.bind_service, R.id.unbind_service, R.id.send_message})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bind_service:
                startFloatingService(this, serviceConnection);
                break;
            case R.id.send_message:
                if (myAidlInterface != null) {
                    try {

                        myAidlBean.code = 0;
                        myAidlBean.message = "hello world";
                        myAidlInterface.sendMessage(myAidlBean);

                        myAidlBean.message = "hello";
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case R.id.unbind_service:
                unbindService(serviceConnection);
//                stopService(new Intent(TestBindServiceActivity.this, FloatWindowService.class));
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (myAidlInterface != null && myAidlInterface.asBinder().isBinderAlive()) {
            try {
                myAidlInterface.unregisterReceiveListener(messageReceiver);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    }

    IBinder.DeathRecipient deathRecipient =
            new IBinder.DeathRecipient() {
                @Override
                public void binderDied() {
                    LogUtils.d("binderDied: ");
                    if (myAidlInterface != null) {
                        myAidlInterface.asBinder().unlinkToDeath(this, 0);
                        myAidlInterface = null;
                    }
                    if (serviceConnection != null) {
                        startFloatingService(TestBindServiceActivity.this, serviceConnection);
                    }
                }
            };

    // 消息监听回调接口
    private IMyAidlInterfaceReceiver messageReceiver =
            new IMyAidlInterfaceReceiver.Stub() {
                @Override
                public void onMessageReceived(MyAidlBean MyAidlBean) throws RemoteException {
                    LogUtils.d("onMessageReceived:" + MyAidlBean);
                }
            };

    private IMyAidlInterface myAidlInterface;

    ServiceConnection serviceConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
                    try {
                        LogUtils.d(
                                "onServiceConnected:ComponentName="
                                        + name.toShortString()
                                        + " IBinder="
                                        + service.getInterfaceDescriptor());
                        myAidlInterface.registerReceiveListener(messageReceiver);
                        myAidlInterface.sendSingleMessage("onServiceConnected");
                        myAidlInterface.asBinder().linkToDeath(deathRecipient, 0);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    //                    myAidlInterface = null;
                    LogUtils.d("onServiceDisconnected:ComponentName=" + name.toShortString());
                }

                @Override
                public void onBindingDied(ComponentName name) {
                    LogUtils.d("onBindingDied:ComponentName=" + name.toShortString());
                }

                @Override
                public void onNullBinding(ComponentName name) {
                    LogUtils.d("onNullBinding:ComponentName=" + name.toShortString());
                }
            };
}
