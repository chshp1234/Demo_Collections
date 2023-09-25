package com.example.aidltest;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.os.Parcel;
import android.os.RemoteCallbackList;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.ArrayMap;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;


import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.example.aidltest.bean.MyAidlBean;
import com.example.aidltest.utils.NotificationUtils;


import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
import static android.view.WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
import static android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
import static android.view.WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON;
import static com.example.aidltest.utils.NotificationUtils.CHANNEL_IDENTIFIER_TASK;

import androidx.collection.CircularArray;
import androidx.collection.SparseArrayCompat;
import androidx.core.util.SparseArrayKt;

public class FloatWindowService extends Service {
    private int movedXDistanceBtn;
    private int movedYDistanceBtn;

    private Button button;
    private WindowManager.LayoutParams layoutParams;
    private WindowManager windowManager;
    private View view;

    public static AtomicBoolean isRun = new AtomicBoolean(false);

    RemoteCallbackList<IMyAidlInterfaceReceiver> callbackList = new RemoteCallbackList<>();

    private MyAidlBean myAidlBean;

    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            /*while (true){
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (myAidlBean!=null){
                    myAidlBean.message = "change";
                }
                LogUtils.d(myAidlBean);
            }*/

        }
    });
    IBinder binder =
            new IMyAidlInterface.Stub() {


                @Override
                public IBinder asBinder() {
                    return super.asBinder();
                }

                @Override
                public boolean onTransact(int code, Parcel data, Parcel reply, int flags)
                        throws RemoteException {
                    String[] packagesForUid =
                            getPackageManager().getPackagesForUid(getCallingUid());

                    LogUtils.d("onTransact: " + Arrays.toString(packagesForUid));
                    return super.onTransact(code, data, reply, flags);
                }

                @Override
                public void basicTypes(
                        int anInt,
                        long aLong,
                        boolean aBoolean,
                        float aFloat,
                        double aDouble,
                        String aString)
                        throws RemoteException {
                    LogUtils.d(
                            "basicTypes: anInt="
                                    + anInt
                                    + " aLong="
                                    + aLong
                                    + " aBoolean="
                                    + aBoolean
                                    + " aFloat="
                                    + aFloat
                                    + " aDouble="
                                    + aDouble
                                    + " aString="
                                    + aString);
                }

                @Override
                public void sendSingleMessage(String message) throws RemoteException {
                    LogUtils.d("sendSingleMessage: message=" + message);
                }

                @Override
                public void sendMessage(MyAidlBean MyAidlBean) throws RemoteException {
                    myAidlBean = MyAidlBean;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    LogUtils.d("sendMessage: MyAidlBean=" + MyAidlBean);
                    MyAidlBean.message = "world";
                }

                @Override
                public void registerReceiveListener(IMyAidlInterfaceReceiver messageReceiver)
                        throws RemoteException {
                    LogUtils.d(
                            "registerReceiveListener: "
                                    + messageReceiver.asBinder().getInterfaceDescriptor());
                    callbackList.register(messageReceiver);
                    MyAidlBean myAidlBean = new MyAidlBean();
                    myAidlBean.code = 0;
                    myAidlBean.message = "registerReceiveListener success";
                    messageReceiver.onMessageReceived(myAidlBean);
                }

                @Override
                public void unregisterReceiveListener(IMyAidlInterfaceReceiver messageReceiver)
                        throws RemoteException {
                    LogUtils.d(
                            "unregisterReceiveListener: "
                                    + messageReceiver.asBinder().getInterfaceDescriptor());
                    callbackList.unregister(messageReceiver);

                    MyAidlBean myAidlBean = new MyAidlBean();
                    myAidlBean.code = 0;
                    myAidlBean.message = "unregisterReceiveListener success";
                    messageReceiver.onMessageReceived(myAidlBean);
                }
            };

    @Override
    public void onCreate() {
        LogUtils.d("FloatWindowService:onCreate");
        startWithFront();
        showFloatingWindow();
        thread.start();
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogUtils.d("FloatWindowService:onStartCommand");
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        LogUtils.d("FloatWindowService:onBind");
        // 自定义permission方式检查权限
        /*if (checkCallingOrSelfPermission("com.example.aidl.permission.REMOTE_SERVICE_PERMISSION")
                == PackageManager.PERMISSION_DENIED) {
            return null;
        }*/
        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        LogUtils.d("FloatWindowService:onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();
        removeFloatingWindow();
    }

    private void startWithFront() {

        Notification notification =
                NotificationUtils.getInstance().getNotificationTask("已开启任务", "已开启任务");
        /*notification.defaults = Notification.DEFAULT_SOUND;*/
        // 设置为默认的声音
        // 参数一：唯一的通知标识；参数二：通知消息。
        startForeground(CHANNEL_IDENTIFIER_TASK, notification); // 开始前台服务
    }

    private void showFloatingWindow() {
        if (Settings.canDrawOverlays(this)) {

            // 获取WindowManager服务
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            view = LayoutInflater.from(this).inflate(R.layout.float_view, null, false);
            button = view.findViewById(R.id.float_bt);
            button.setOnTouchListener(new FloatingOnTouchListener());
            button.setOnClickListener(
                    (v) -> {
                        if (movedXDistanceBtn <= 10 && movedYDistanceBtn <= 10) {
                            stopSelf();
                            //                            onDestroy();

                            isRun.getAndSet(false);
                        }
                    });
            // 设置LayoutParam
            layoutParams = new WindowManager.LayoutParams();
            /*if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N_MR1) {
                            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                            layoutParams.type = WindowManager.LayoutParams.TYPE_TOAST;
                        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
                            Context topActivityOrApp = ActivityUtils.getTopActivity();
                            if (topActivityOrApp == null) {
                                topActivityOrApp=Utils.getApp();
                            }
                            if (!(topActivityOrApp instanceof Activity)) {
                                Log.e("ToastUtils", "Couldn't get top Activity.");
                                return;
                            }
                            Activity topActivity = (Activity) topActivityOrApp;
                            if (topActivity.isFinishing() || topActivity.isDestroyed()) {
                                Log.e("ToastUtils", topActivity + " is useless");
                                return;
                            }
                            windowManager = topActivity.getWindowManager();
                            layoutParams.type = WindowManager.LayoutParams.LAST_APPLICATION_WINDOW;
            //                Utils.getActivityLifecycle().addOnActivityDestroyedListener(topActivity, LISTENER);
                        } else {
                            windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                            layoutParams.type = WindowManager.LayoutParams.FIRST_SYSTEM_WINDOW + 37;
                        }*/
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.TRANSLUCENT;
            /*layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;*/
            layoutParams.width = SizeUtils.dp2px(50);
            layoutParams.height = SizeUtils.dp2px(50);
            //                layoutParams.x = 300;
            //                layoutParams.y = 300;
            //                layoutParams.flags = layoutParams.flags |
            // WindowManager.LayoutParams.FLAG_FULLSCREEN;
            layoutParams.gravity = Gravity.CENTER_VERTICAL | Gravity.END;
            layoutParams.flags =
                    /*FLAG_NOT_FOCUSABLE
                    | FLAG_NOT_TOUCHABLE
                    | */ FLAG_KEEP_SCREEN_ON
                    | FLAG_LAYOUT_IN_SCREEN
                    | FLAG_NOT_FOCUSABLE /*可实现屏幕无法点击，但是可以用无障碍服务进行操作*/
                    | FLAG_TURN_SCREEN_ON;
            // 将悬浮窗控件添加到WindowManager
            windowManager.addView(view, layoutParams);

            /*// 获取WindowManager服务
            windowManagerBtn = (WindowManager) getSystemService(WINDOW_SERVICE);
            viewBtn = LayoutInflater.from(this).inflate(R.layout.float_view_btn, null, false);
            AppCompatButton button = viewBtn.findViewById(R.id.float_bt);
            //                button.setOnTouchListener(new FloatingOnTouchListener());
            button.setOnClickListener(
                    (v) -> {
                        if (movedXDistanceBtn <= 10 && movedYDistanceBtn <= 10) {
                            ToastUtils.showShort("你好?");
                        }
                    });
            // 设置LayoutParam
            layoutParamsBtn = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParamsBtn.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParamsBtn.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParamsBtn.format = PixelFormat.TRANSLUCENT;
            */
            /*layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE |
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;*/
            /*
            layoutParamsBtn.width = SizeUtils.dp2px(100);
            layoutParamsBtn.height = SizeUtils.dp2px(100);
            //            layoutParamsBtn.x = 300;
            layoutParamsBtn.y = SizeUtils.dp2px(100);
            //                layoutParams.flags = layoutParams.flags |
            // WindowManager.LayoutParams.FLAG_FULLSCREEN;
            layoutParamsBtn.gravity = Gravity.CENTER;
            //            layoutParams.y = SizeUtils.dp2px(-100);
            layoutParamsBtn.flags = FLAG_NOT_FOCUSABLE | FLAG_NOT_TOUCH_MODAL;
            // 将悬浮窗控件添加到WindowManager
            windowManagerBtn.addView(viewBtn, layoutParamsBtn);*/
        }
    }

    private void removeFloatingWindow() {
        if (windowManager != null) {
            view.setOnTouchListener(null);
            windowManager.removeViewImmediate(view);
            windowManager = null;
        }
    }

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        private int movedX;
        private int movedY;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    LogUtils.d("ACTION_DOWN");
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    movedXDistanceBtn = 0;
                    movedYDistanceBtn = 0;
                    break;
                case MotionEvent.ACTION_MOVE:
                    LogUtils.d("ACTION_MOVE");

                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    movedX = x - nowX;
                    movedY = nowY - y;

                    movedXDistanceBtn = Math.abs(movedX) + movedXDistanceBtn;
                    movedYDistanceBtn = Math.abs(movedY) + movedYDistanceBtn;
                    //                    LogUtils.d(
                    //                            "movedXDistance="
                    //                                    + movedXDistanceBtn
                    //                                    + "\t movedYDistance="
                    //                                    + movedYDistanceBtn);
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;

                    // 更新悬浮窗控件布局

                    if (windowManager != null) {
                        windowManager.updateViewLayout(view, layoutParams);
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    LogUtils.d("ACTION_UP");
                    break;
                default:
                    break;
            }
            return false;
        }
    }
}
