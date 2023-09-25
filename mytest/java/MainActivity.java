package com.example.aidltest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.media.AudioRecord;
import android.media.MediaExtractor;
import android.media.MediaFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.util.Pair;

import com.InitData;
import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.UriUtils;
import com.example.aidltest.CustomView.CountdownView;
import com.example.aidltest.CustomView.CustomScrollView;
import com.example.aidltest.CustomView.GoodsSortView;
import com.example.aidltest.CustomView.Rotate3dAnimation;
import com.example.aidltest.CustomView.SearchEdit;
import com.example.aidltest.base.BaseActivity;
import com.example.aidltest.config.FilePathConfig;
import com.example.aidltest.http.HttpObservable;
import com.example.aidltest.http.RetrofitFactory;
import com.example.aidltest.utils.AccessibilityCheckUtils;
import com.example.aidltest.utils.ClipboardUtils;
import com.example.aidltest.utils.MotionEventUtils;
import com.example.aidltest.utils.QRCodeDecoderUtils;
import com.taotaosou.accessibility.utils.AssistUtil;
import com.taotaosou.accessibility.viewid.TaoBWidgetId;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

import static android.content.Intent.ACTION_MEDIA_SCANNER_FINISHED;
import static android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE;
import static android.content.Intent.ACTION_MEDIA_SCANNER_STARTED;
import static com.example.aidltest.AutoScriptService.CURRENT_PACKAGE;
import static com.example.aidltest.MainActivity.NotificationBroadcastReceiver.ACTION_CANCEL;
import static com.example.aidltest.MainActivity.NotificationBroadcastReceiver.ACTION_CLICK;
import static com.example.aidltest.config.FilePathConfig.APP_AUDIO;
import static com.example.aidltest.config.RequestCodeConfig.REQUEST_ACCESSIBILITY_CODE;
import static com.example.aidltest.config.RequestCodeConfig.REQUEST_FLOATING_CODE;
import static com.taotaosou.accessibility.utils.AssistUtil.assistService;
import static com.taotaosou.accessibility.utils.AssistUtil.getNodeInfoTextByNode;
import static com.taotaosou.accessibility.utils.AssistUtil.getNodeInfosByViewId;
import static com.taotaosou.accessibility.utils.AssistUtil.getRootNode;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoClick;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScroll;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScrollBackward;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScrollForward;
import static com.taotaosou.accessibility.utils.AssistUtil.nodePerformInfoClick;
import static com.taotaosou.accessibility.utils.AssistUtil.nodePerformInfoScrollBackward;
import static com.taotaosou.accessibility.utils.AssistUtil.nodePerformInfoScrollForward;

public class MainActivity extends BaseActivity {

    public static final long ONE_SECOND = 1000L;
    public static final long ONE_MINUTE = 60 * ONE_SECOND;

    @BindView(R.id.test)
    TextView test;

    @BindView(R.id.as_state)
    TextView asState;

    @BindView(R.id.count_down)
    CountdownView countdownView;

    @BindView(R.id.test_notification)
    Button testNotification;

    @BindView(R.id.goods_sort)
    GoodsSortView goodsSortView;

    @BindView(R.id.lottieAnimationView)
    LottieAnimationView lottieAnimationView;

    @BindView(R.id.search_edit)
    SearchEdit searchEdit;

    @BindView(R.id.search_text)
    TextView searchText;

    @BindView(R.id.customScrollView)
    CustomScrollView customScrollView;

    private MyBroadCast broadCast;
    private PrintViewBroadCast printViewBroadCast;
    private clickNodeBroadCast clickNodeBroadCast;
    private ArrayList<Long> timeInterval;
    private ArrayList<String> timeList;
    private int count;

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);

    }

    /** 斐波那契函数 */
    private int Fibonacci(int i) {

        if (i < 0) {
            return -1;
        }
        if (i == 0) {
            return 0;
        }
        if (i == 1 || i == 2) {
            return 1;
        }

        Matrix matrix = new Matrix();

        return Fibonacci(i - 1) + Fibonacci(i - 2);
    }

    /**
     * 大于给定数的最小二次幂数 Allocates empty array to hold the given number of elements.
     *
     * @param numElements the number of elements to hold
     */
    private int LeastPowerOfTwo(int numElements) {
        int initialCapacity = 0;
        // Find the best power of two to hold elements.
        // Tests "<=" because arrays aren't kept full.
        if (numElements >= initialCapacity) {
            initialCapacity = numElements;
            initialCapacity |= (initialCapacity >>> 1);
            initialCapacity |= (initialCapacity >>> 2);
            initialCapacity |= (initialCapacity >>> 4);
            initialCapacity |= (initialCapacity >>> 8);
            initialCapacity |= (initialCapacity >>> 16);
            initialCapacity++;

            if (initialCapacity < 0) // Too many elements, must back off
            initialCapacity >>>= 1; // Good luck allocating 2^30 elements
        }
        return initialCapacity;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ACCESSIBILITY_CODE) {
            boolean asIsEnable =
                    AccessibilityCheckUtils.isAccessibilityEnable(
                            getApplicationContext(),
                            this.getPackageName()
                                    + "/"
                                    + AutoScriptService.class.getCanonicalName());
            asState.setText("" + asIsEnable);
            if (asIsEnable) {
                asState.setTextColor(getColor(R.color.green));
            } else {
                asState.setTextColor(getColor(R.color.red));
            }
        } else if (requestCode == 10001) {
            LogUtils.d("requestCode=" + requestCode);
            LogUtils.d("resultCode=" + resultCode);
            getIntent(data);
            getIntentData(data);
            if (data != null) {
                cropPic(data.getData());
            }
        } else if (requestCode == 10010) {
            LogUtils.d("requestCode=" + requestCode);
            LogUtils.d("resultCode=" + resultCode);
            getIntent(data);
            getIntentData(data);

            // 用户头像剪裁后
            Bitmap bitmap = data.getExtras().getParcelable("data");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片
     *
     * @param data
     */
    private void cropPic(Uri data) {
        if (data == null) {
            return;
        }
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(data, "image/*");

        // 开启裁剪：打开的Intent所显示的View可裁剪
        cropIntent.putExtra("crop", "true");
        // 裁剪宽高比
        cropIntent.putExtra("aspectX", 1);
        cropIntent.putExtra("aspectY", 1);
        // 裁剪输出大小
        cropIntent.putExtra("outputX", 320);
        cropIntent.putExtra("outputY", 320);
        cropIntent.putExtra("scale", true);
        /**
         * return-data 这个属性决定我们在 onActivityResult 中接收到的是什么数据， 如果设置为true 那么data将会返回一个bitmap
         * 如果设置为false，则会将图片保存到本地并将对应的uri返回，当然这个uri得有我们自己设定。
         * 系统裁剪完成后将会将裁剪完成的图片保存在我们所这设定这个uri地址上。我们只需要在裁剪完成后直接调用该uri来设置图片，就可以了。
         */
        cropIntent.putExtra("return-data", true);
        // 当 return-data 为 false 的时候需要设置这句
        //        cropIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        // 图片输出格式
        //        cropIntent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        // 头像识别 会启动系统的拍照时人脸识别
        //        cropIntent.putExtra("noFaceDetection", true);
        startActivityForResult(cropIntent, 10010);
    }

    public static void getIntentData(Intent data) {
        if (data != null) {
            Uri uri = data.getData();
            if (uri != null) {

                File file = UriUtils.uri2File(uri);
                LogUtils.d("getIntentData: file=" + file.getAbsolutePath());
            }
        }
    }

    public static void getIntent(Intent data) {
        if (data != null) {
            Bundle bundle = data.getExtras();
            if (bundle != null) {
                Iterator<String> iterator = bundle.keySet().iterator();
                String key;
                while (iterator.hasNext()) {
                    key = iterator.next();
                    LogUtils.d(
                            "getIntent: "
                                    + key
                                    + "("
                                    + bundle.get(key).getClass().getSimpleName()
                                    + ")"
                                    + "="
                                    + String.valueOf(bundle.get(key)));
                }
            }
        }
    }

    public static final String IMAGE_UNSPECIFIED = "image/*"; // 随意图片类型

    @SuppressLint("RestrictedApi")
    @OnClick(R.id.count_down)
    public void onClick(View view) {
        ToastUtils.showShort("点点点~");
        LogUtils.d("onClick: " + ClipboardUtils.getText());

        ActivityUtils.startActivity(TestBindServiceActivity.class);

        /*Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, IMAGE_UNSPECIFIED);
        startActivityForResult(intent, 10001);*/
        /*Intent imgIntent = new Intent();
        imgIntent.setAction(Intent.ACTION_PICK);
        imgIntent.setType("image/*");
        startActivityForResult(imgIntent, 10001);*/
        // 内存监控
        /*memHandlerThread = new HandlerThread("MemoryMonitoring");
        memHandlerThread.start();
        memHandler =
                new Handler(memHandlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        LogFileUtils.fileSystem(
                                "MemoryMonitoring",
                                "start==========================================================\n"
                                        + MemoryMonitorUtils.getInstance().checkAllMemory()
                                        + "\nend==========================================================\n");
                        memHandler.postDelayed(null, TimeConfig.MIN);
                    }
                };
        memHandler.post(null);*/

        // 设置渠道
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "chat";
            String channelName = "聊天消息";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "subscribe";
            channelName = "订阅消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);

            channelId = "recommend";
            channelName = "推荐消息";
            importance = NotificationManager.IMPORTANCE_DEFAULT;
            createNotificationChannel(channelId, channelName, importance);
        }*/

        //        ShellUtils.CommandResult commandResult =
        //                ShellUtils.execCmd("pm list package -3", false, true);
        //        LogUtils.d(commandResult);

        /*Map<String, String> app = getApps();
        Set<Map.Entry<String, String>> entries = app.entrySet();
        for (Map.Entry<String, String> entry : entries) {
            LogUtils.d("package:" + entry.getKey() + "\n" + "name:" + entry.getValue());
        }*/

        /*List<AppUtils.AppInfo> appsInfo = AppUtils.getAppsInfo();
        for (AppUtils.AppInfo appInfo : appsInfo) {
            if (!appInfo.isSystem()) {
                LogUtils.d(appInfo);
            }
        }*/

        /*LogUtils.d(
                "onClick: PermissionUtils.isGrantedDrawOverlays():"
                        + PermissionUtils.isGrantedDrawOverlays());
        LogUtils.d(
                "onClick: Settings.canDrawOverlays(Utils.getApp()):"
                        + Settings.canDrawOverlays(Utils.getApp()));
        LogUtils.d("onClick:AccessUtils.isCanDrawOverlays: " + AccessUtils.isCanDrawOverlays(this));
        if (!AccessUtils.isCanDrawOverlays(this)) {
            AccessUtils.getOverlay(this);
        } else {
            startService(new Intent(this, FloatWindowService.class));
        }
        startService(new Intent(this, FloatWindowService.class));*/

        /*Utils.getApp()
        .startService(new Intent(MyApplication.getContext(), FloatWindowService.class));*/
        //        ToastUtils.showShort("弹弹悬浮窗");

        //        LogUtils.d("onClick: " + new XToast(this).isShow());

        //        LogUtils.d("isDeviceRooted:" + DeviceUtils.isDeviceRooted());
        //        LogUtils.d("isAdbEnabled:" + DeviceUtils.isAdbEnabled());
        //        LogUtils.d("getSDKVersionName:" + DeviceUtils.getSDKVersionName());
        //        LogUtils.d("getSDKVersionCode:" + DeviceUtils.getSDKVersionCode());
        //        LogUtils.d("getAndroidID:" + DeviceUtils.getAndroidID());
        //        LogUtils.d("getMacAddress:" + DeviceUtils.getMacAddress());
        //        LogUtils.d("getManufacturer:" + DeviceUtils.getManufacturer());
        //        LogUtils.d("getModel:" + DeviceUtils.getModel());
        //        //        LogUtils.d("getDeviceId:" + PhoneUtils.getDeviceId());
        //        //        LogUtils.d("getSerial:" + PhoneUtils.getSerial());
        //        //        LogUtils.d("getIMEI:" + PhoneUtils.getIMEI());
        //        //        LogUtils.d("getIMSI:" + PhoneUtils.getIMSI());
        //        //        LogUtils.d("getMEID:" + PhoneUtils.getMEID());
        //        LogUtils.d("getIpAddressByWifi:" + NetworkUtils.getIpAddressByWifi());
        //        LogUtils.d("getServerAddressByWifi:" + NetworkUtils.getServerAddressByWifi());
        //        LogUtils.d("getBroadcastIpAddress:" + NetworkUtils.getBroadcastIpAddress());
        //        LogUtils.d("getIPAddress:" + NetworkUtils.getIPAddress(true));
        //        LogUtils.d("IpCheckUtils.getIPAddress:" + IpCheckUtils.getIPAddress(this));
        //        LogUtils.d("IpCheckUtils.getNetIp:" + IpCheckUtils.getNetIp());

        //        new Thread(() -> LogUtils.d(getNetIp())).start();

        /*HttpObservable.getObservable(
                        RetrofitFactory.getApiService()
                                .getAddress("http://pv.sohu.com/cityjson?ie=utf-8"))
                .subscribe(
                        new Observer<String>() {
                            @Override
                            public void onSubscribe(Disposable d) {}

                            @Override
                            public void onNext(String s) {
                                LogUtils.d("onNext: " + s);
                            }

                            @Override
                            public void onError(Throwable e) {}

                            @Override
                            public void onComplete() {}
                        });*/

        //
        //        ScreenShotUtils.getInstance().beginScreenShot(this);

        /*if (!AccessibilityCheckUtils.isAccessibilityEnable(
                getApplicationContext(),
                getPackageName() + "/" + AutoScriptService.class.getCanonicalName())) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivityForResult(intent, 11);
        }*/

        // 悬浮窗权限
        /*if (!Settings.canDrawOverlays(MainActivity.this)) {
            ToastUtils.showShort("当前无权限，请授权");
            startActivityForResult(
                    new Intent(
                            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + getPackageName())),
                    REQUEST_FLOATING_CODE);
        }*/

        // 截屏权限
        /*ScreenShotUtils.getInstance().requestCapturePermission(MainActivity.this);*/

        // 文件读写权限
        /*PermissionUtils.permission(PermissionConstants.STORAGE)
                .callback(
                        new PermissionUtils.FullCallback() {
                            @Override
                            public void onGranted(List<String> permissionsGranted) {
                                ToastUtils.showShort("文件读写权限授权成功");
                            }

                            @Override
                            public void onDenied(
                                    List<String> permissionsDeniedForever,
                                    List<String> permissionsDenied) {
                                ToastUtils.showShort("文件读写权限授权失败");
                            }
                        })
                .request();

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK), REQUEST_FLOATING_CODE);*/

        /*RequestBean bean = new RequestBean();
        bean.setcNum(12);
        bean.setfNum(13);
        bean.setTime(1550031009369L);
        LogUtils.d("onClick: " + SignUtils.getSign(bean, "asdf!@_"));

        LogUtils.d("onClick: " + g.pd);

        timeList = new ArrayList<>();
        timeList.add("2019-02-15 15:00:00");
        timeList.add("2019-02-15 15:00:03");
        timeList.add("2019-02-15 15:00:15");
        timeList.add("2019-02-15 15:00:20");
        timeList.add("2019-02-15 15:00:30");

        long delay = TimeUtils.string2Millis(timeList.get(0)) - 1550213995000L;
        timeInterval = new ArrayList<>();
        timeInterval.add(delay);
        for (int i = 0, l = timeList.size() - 1; i < l; i++) {
            long first = TimeUtils.string2Millis(timeList.get(i));
            long sec = TimeUtils.string2Millis(timeList.get(i + 1));
            timeInterval.add(sec - first);
        }
        LogUtils.d("onClick: " + timeInterval);

        count = timeList.size();
        handlerInterval.postDelayed(null, timeInterval.get(0));*/

        // 跳转淘宝商品详情页
        /*Intent intentD = new Intent();
        intentD.setAction("Android.intent.action.VIEW");
        Uri uriD =
                Uri.parse(
                        "https://detail.tmall.com/item.htm?spm=a21ag.7623864.0.0.481b3ba5CXxVSf&id=614665920076"); // 商品地址
        intentD.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentD.setData(uriD);
        intentD.setClassName(
                "com.taobao.taobao", "com.taobao.android.detail.wrapper.activity.DetailActivity");
        startActivity(intentD);*/

        // 跳转淘宝搜索页
        //        Intent intent = new Intent();
        //        intent.setAction("Android.intent.action.VIEW");
        //        //
        //        //
        // "https://s.m.taobao.com/h5?q=北欧简约轻奢风窗帘天鹅绒纯色卧室客厅遮光隔音绒布窗帘成品定制&start_price=20&end_price=208&loc=绍兴"
        //        Uri uri =
        //                Uri.parse(
        //
        // "https://s.m.taobao.com/h5?q=长袖&start_price=0&end_price=500&sort=_bid&loc=广州&style=list"); // 商品地址
        //        intent.setData(uri);
        //        intent.setClassName("com.taobao.taobao",
        // "com.taobao.search.sf.MainSearchResultActivity");
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        startActivity(intent);

        // 跳转淘宝网页
        //        Intent intent = new Intent();
        //        intent.setAction("Android.intent.action.VIEW");
        //        Uri uri = Uri.parse("https://rate.taobao.com/myRate.htm"); // 商品地址
        //        intent.setData(uri);
        //        intent.setClassName("com.taobao.taobao", "com.taobao.browser.BrowserActivity");
        //        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //        startActivity(intent);

        /*String price = "asdf16.9";
        StringBuilder start = new StringBuilder();
        StringBuilder end = new StringBuilder();
        boolean startMatchFirst = false;
        boolean endMatchFirst = false;
        for (int i = 0, l = price.length(); i < l; i++) {
            if (price.charAt(i) >= 48 && price.charAt(i) <= 57) {
                startMatchFirst = true;
                if (!endMatchFirst) {
                    start.append(price.charAt(i));
                } else {
                    end.append(price.charAt(i));
                }
            } else if (price.charAt(i) == 46) {
                if (!endMatchFirst) {
                    start.append(price.charAt(i));
                } else {
                    end.append(price.charAt(i));
                }
            } else {
                if (startMatchFirst) {
                    endMatchFirst = true;
                }
            }
        }
        LogUtils.d("onClick: start=" + start);
        LogUtils.d("onClick: end=" + end);*/

        /*Intent intent = new Intent("com.taobao.tao.search.SearchDoor");
        intent.setClassName("com.taobao.taobao", "com.taobao.search.searchdoor.SearchDoorActivity");
        AssistUtil.gotoTargetActivity(intent);*/
        //        LogUtils.d("onClick: " + Arrays.toString(com.ss.sys.secuni.b.c.n0(this)));
        //        LogUtils.d(
        //                "onClick: "
        //                        + Md5Utils.a(
        //
        // "manifest_version_code=660&_rticket=1559796593053&iid=74358663151"
        //                                        +
        // "&channel=huawei_1&device_type=LON-AL00&language=zh&uuid=865034030414232"
        //                                        +
        // "&resolution=1440*2560&openudid=a50060da3ef95ab7&update_version_code=6602"
        //                                        +
        // "&password=34373631&os_api=28&dpi=640&retry_type=no_retry&ac=wifi"
        //                                        +
        // "&device_id=47684468096&mcc_mnc=46001&mix_mode=1&os_version=9"
        //                                        +
        // "&mobile=2e3d332534363725363032342533323334&version_code=660&app_name=aweme"
        //                                        +
        // "&account_sdk_source=app&version_name=6.6.0&js_sdk_version=1.16.2.7"
        //                                        +
        // "&device_brand=HUAWEI&ssmix=a&device_platform=android&aid=1128&ts=1559796588"));
        //        LogUtils.d(
        //                "onClick: "
        //                        + k.a(
        //                                a.leviathan(
        //                                        1559796593,
        //                                        k.a(
        //                                                Md5Utils.a(
        //
        // "account_sdk_version=362&manifest_version_code=660"
        //                                                                        +
        // "&_rticket=1559796593053&iid=74358663151&channel=huawei_1"
        //                                                                        +
        // "&device_type=LON-AL00&language=zh&uuid=865034030414232"
        //                                                                        +
        // "&resolution=1440*2560&openudid=a50060da3ef95ab7"
        //                                                                        +
        // "&update_version_code=6602&os_api=28&dpi=640&ac=wifi&device_id=47684468096"
        //                                                                        +
        // "&mcc_mnc=46001&os_version=9&version_code=660&app_name=aweme"
        //                                                                        +
        // "&version_name=6.6.0&js_sdk_version=1.16.2.7&device_brand=HUAWEI&ssmix=a"
        //                                                                        +
        // "&device_platform=android&aid=1128&ts=1559796588")
        //                                                        /*X-SS-STUB*/
        //                                                        +
        // "CD804D7E571623A51CAFF45852FD725D"
        //                                                        /*cookie*/
        //                                                        + Md5Utils.a(
        //                                                                "install_id=74274709555;
        // ttreq=1$2fbbd811d92ff7b6525be3add11da28a24af7d75; "
        //                                                                        + "qh[360]=1;
        // sid_guard=ffc9c5012014153e5505f4baa45a9d71%7C1559796452%7C21600%7CThu%2C+06-Jun-2019+10%3A47%3A32+GMT; "
        //                                                                        +
        // "uid_tt=1a498f580c0f140269472206d01668c0; sid_tt=ffc9c5012014153e5505f4baa45a9d71; "
        //                                                                        +
        // "sessionid=ffc9c5012014153e5505f4baa45a9d71; "
        //                                                                        +
        // "odin_tt=f47dffdfb55792a54f44c8a496ddf792d0a9aacb7f5311d83e21140466c834a5fc8f83beff265cc3705bcc3a29e20a60")
        //                                                        /*TT.format_session_id(cookie)*/
        //                                                        + Md5Utils.a(
        //
        // "ffc9c5012014153e5505f4baa45a9d71"
        //                                                                        +
        // "odin_tt=f47dffdfb55792a54f44c8a496ddf792d0a9aacb7f5311d83e21140466c834a5fc8f83beff265cc3705bcc3a29e20a60")))));

        /*Observable.interval(0, 100, TimeUnit.MILLISECONDS)
        // 设置循环11次
        .doOnSubscribe(
                disposable -> {
                    // 在发送数据的时候设置为不能点击
                })
        // 操作UI主要在UI线程
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(
                aLong -> {
                    LogUtils.d("onClick: " + aLong);
                });*/

        /*DropBoxManager dropBoxManager = MyApplication.getContext().getSystemService(DropBoxManager.class);
        DropBoxManager.Entry nextEntry = dropBoxManager.getNextEntry(null, 1562720000);
        LogUtils.d("onClick: "+nextEntry);*/

        /*try {
            throwException(1);
        } catch (Exception e) {
            LogUtils.e("onClick: ", e);
            throw e;
        } finally {
            LogUtils.d("onClick: finally");
        }*/

        // CyclicBarrier
        /*CyclicBarrier cyclicBarrier = new CyclicBarrier(3, () -> LogUtils.d("run: open barrier"));
        new Thread(
                        () -> {
                            int i = 3;
                            while (i >= 0) {
                                LogUtils.d("run: a " + i);
                                sleep(1000L);
                                i--;
                            }
                            try {
                                cyclicBarrier.await();
                                LogUtils.d("run: a finish");
                            } catch (BrokenBarrierException e) {
                                LogUtils.e("onClick: ", e);
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                LogUtils.e("onClick: ", e);
                                e.printStackTrace();
                            }
                        })
                .start();

        new Thread(
                        () -> {
                            int i = 4;
                            while (i >= 0) {
                                LogUtils.d("run: b " + i);
                                sleep(1000L);
                                i--;
                            }
                            try {
                                cyclicBarrier.await();
                                LogUtils.d("run: b finish");
                            } catch (BrokenBarrierException e) {
                                LogUtils.e("onClick: ", e);
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                LogUtils.e("onClick: ", e);
                                e.printStackTrace();
                            }
                        })
                .start();

        new Thread(
                        () -> {
                            int i = 5;
                            while (i >= 0) {
                                LogUtils.d("run: c " + i);
                                sleep(1000L);
                                i--;
                            }
                            try {
                                cyclicBarrier.await();
                                LogUtils.d("run: c finish");
                            } catch (BrokenBarrierException e) {
                                LogUtils.e("onClick: ", e);
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                LogUtils.e("onClick: ", e);
                                e.printStackTrace();
                            }
                        })
                .start();*/

        /*LogUtils.d("onClick: " + NotificationBroadcastReceiver.class.getName());
        Intent intentClick = new Intent(ACTION_CLICK);
        intentClick.putExtra("message", "aaa");
        //        intentClick.setComponent(new ComponentName(PackageInfoConfig.FBOX_PACKAGE_NAME,
        // "com.flowbox.fbox.pushservice.MessageReceiveIntentService$NotificationBroadcastReceiver"));
        sendBroadcast(intentClick, "com.timemm.permission.SEND_PUSH_NOTIFICATION");*/

        /*String url = "ws://111.231.101.221:8081/index";
        WebSocket webSocket = WebSocketFactory.getInstance().getWebSocket(url);
        webSocket.send("艹啊小胖=========================");
        //        webSocket.send("艹JJ咯");
        LogUtils.d("onClick: " + ByteString.encodeUtf8("aaa"));*/

        /*LogUtils.d("onReceive: supportsMultipleUsers=" + UserManager.supportsMultipleUsers());

        Class cls = null;
        try {
            cls = Class.forName("android.os.UserHandle");
            int myUserId =
                    ((Integer)
                                    cls.getDeclaredMethod("myUserId", new Class[0])
                                            .invoke(cls, new Object[0]))
                            .intValue();
            LogUtils.d("onClick: myUserId=" + myUserId);

            Drawable drawable = getDrawable(R.drawable.click_star);
            getPackageManager().getUserBadgedIcon(drawable, Process.myUserHandle());

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }*/

        // 打印堆栈
        //        Thread.dumpStack();

        //                ActivityUtils.startActivity(ExoTest.class);
        //        UUID uuid = UUID.randomUUID();
        //        LogUtils.d("onClick: " + uuid.toString());
        //

        //        ToastUtils.showCustomShort(R.layout.toast_img);

        /*String s = new String("s");
        String a = new String("s").intern();
        LogUtils.d("onClick:s=a?: " + (s == a));*/

        /*new Thread(
        new Runnable() {
            @Override
            public void run() {

                String s = new String("sdf");
                s.indexOf("sdfsd");

                Map<String, String> map = new HashMap<>();
                map.put("是否", "1");
                map.put("我发给", "2");
                map.put("微服务", "3");
                map.put("是大法官为", "4");
                map.put("问过", "5");
                map.put("偶奇偶", "6");
                map.put("已耦合", "7");
                map.forEach(
                        new BiConsumer<String, String>() {
                            @Override
                            public void accept(String s, String s2) {
                                LogUtils.d("HashMap.forEach: key=" + s + " value=" + s2);
                            }
                        });

                TreeMap<String, String> treeMap = new TreeMap<>();
                treeMap.put("D", "4");
                treeMap.put("A", "1");
                treeMap.put("G", "7");
                treeMap.put("C", "3");
                treeMap.put("B", "2");
                treeMap.put("F", "6");
                treeMap.put("E", "5");
                treeMap.forEach(
                        new BiConsumer<String, String>() {
                            @Override
                            public void accept(String s, String s2) {
                                LogUtils.d("TreeMap.forEach: key=" + s + " value=" + s2);
                            }
                        });

                treeMap.putAll(map);
                treeMap.forEach(
                        new BiConsumer<String, String>() {
                            @Override
                            public void accept(String s, String s2) {
                                LogUtils.d(
                                        "TreeMap.putAll.forEach: key="
                                                + s
                                                + " value="
                                                + s2);
                            }
                        });

                treeMap.clear();
                treeMap.putAll(map);
                treeMap.forEach(
                        new BiConsumer<String, String>() {
                            @Override
                            public void accept(String s, String s2) {
                                LogUtils.d(
                                        "TreeMap.clear.putAll.forEach: key="
                                                + s
                                                + " value="
                                                + s2);
                            }
                        });

                treeMap.clear();
                treeMap.put("是否", "1");
                treeMap.put("我发给", "2");
                treeMap.put("微服务", "3");
                treeMap.put("是大法官为", "4");
                treeMap.put("问过", "5");
                treeMap.put("偶奇偶", "6");
                treeMap.put("已耦合", "7");
                treeMap.forEach(
                        new BiConsumer<String, String>() {
                            @Override
                            public void accept(String s, String s2) {
                                LogUtils.d(
                                        "TreeMap.clear.put.forEach: key="
                                                + s
                                                + " value="
                                                + s2);
                            }
                        });

                try (Stream<String> stringStream = treeMap.keySet().stream()) {

                    stringStream
                            .map(
                                    new Function<String, Integer>() {
                                        @Override
                                        public Integer apply(String st) {
                                            return st.hashCode();
                                        }
                                    })
                            .flatMap(
                                    new Function<Integer, Stream<String>>() {
                                        @Override
                                        public Stream<String> apply(Integer integer) {
                                            return null;
                                        }
                                    });

                    LogUtils.d(
                            stringStream
                                    .min(
                                            new Comparator<String>() {
                                                @Override
                                                public int compare(String o1, String o2) {
                                                    return o1.hashCode() - o2.hashCode();
                                                }
                                            })
                                    .map(
                                            new Function<String, Optional<String>>() {
                                                @Override
                                                public Optional<String> apply(String s) {
                                                    return Optional.of(s);
                                                }
                                            })
                                    .flatMap(
                                            new Function<
                                                    Optional<String>, Optional<Integer>>() {
                                                @Override
                                                public Optional<Integer> apply(
                                                        Optional<String> integer) {
                                                    return Optional.of(integer.hashCode());
                                                }
                                            })
                                    .get());
                }
            }
        });*/

        /*B b = new B();
        B b1 = b;
        LogUtils.d("onClick: b1=" + b1.hashCode() + " b=" + b.hashCode());
        b = new B();
        LogUtils.d("onClick: b1=" + b1.hashCode() + " b=" + b.hashCode());*/

        /*int i = 0;
        i = i++;
        LogUtils.d("onClick: " + i);
        i = 0;
        i = ++i;
        LogUtils.d("onClick: " + i);
        i = 0;
        i++;
        LogUtils.d("onClick: " + i);
        i = 0;
        ++i;
        LogUtils.d("onClick: " + i);*/
        /*for (int i = 0; ; i++) {

            LogUtils.d("onClick: " + i);
            if (i == 10) {
                break;
            }
        }*/

        /*map.replaceAll(
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String s, String s2) {

                        return null;
                    }
                });
        map.computeIfAbsent(
                "a",
                new Function<String, String>() {
                    @Override
                    public String apply(String s) {
                        return null;
                    }
                });
        map.compute(
                "b",
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String s, String s2) {
                        return null;
                    }
                });
        map.merge(
                "a",
                "b",
                new BiFunction<String, String, String>() {
                    @Override
                    public String apply(String s, String s2) {
                        return s.concat(s2);
                    }
                });*/

        //        testStream();

        /*class test extends HashMap<String, Integer> {}

        Map<?, Integer> map = new HashMap<String, Integer>();
        LogUtils.d(
                "getGenericSuperclass: "
                        + map.getClass().getGenericSuperclass().getTypeName()
                        + "\n"
                        + "getTypeParameters: "
                        + Arrays.toString(map.getClass().getTypeParameters())
                        + "\n"
                        + "getTypeName: "
                        + map.getClass().getTypeName());
        map = new HashMap<String, Integer>() {};
        LogUtils.d(
                "getGenericSuperclass: "
                        + map.getClass().getGenericSuperclass().getTypeName()
                        + "\n"
                        + "getTypeParameters: "
                        + Arrays.toString(map.getClass().getTypeParameters())
                        + "\n"
                        + "getTypeName: "
                        + map.getClass().getTypeName());

        ParameterizedType parameterizedType =
                (ParameterizedType) map.getClass().getGenericSuperclass();
        LogUtils.d("getTypeName: " + parameterizedType.getTypeName());
        LogUtils.d(
                "getActualTypeArguments: "
                        + Arrays.toString(parameterizedType.getActualTypeArguments()));
        LogUtils.d("getOwnerType: " + parameterizedType.getOwnerType());
        LogUtils.d("getRawType: " + parameterizedType.getRawType());*/

        /*map = new test();
        LogUtils.d("getGenericSuperclass: " + map.getClass().getGenericSuperclass().getTypeName());
        LogUtils.d("getTypeParameters: " + Arrays.toString(map.getClass().getTypeParameters()));
        LogUtils.d("getTypeName: " + map.getClass().getTypeName() + "\n");

        map = new TestHashMap();
        LogUtils.d("getGenericSuperclass: " + map.getClass().getGenericSuperclass().getTypeName());
        LogUtils.d("getTypeParameters: " + Arrays.toString(map.getClass().getTypeParameters()));
        LogUtils.d("getTypeName: " + map.getClass().getTypeName() + "\n");

        map = new com.TestHashMap();
        LogUtils.d("getGenericSuperclass: " + map.getClass().getGenericSuperclass().getTypeName());
        LogUtils.d("getTypeParameters: " + Arrays.toString(map.getClass().getTypeParameters()));
        LogUtils.d("getTypeName: " + map.getClass().getTypeName() + "\n");*/

        /* TestHashMap.var = 6;

        TestHashMap testHashMap = new TestHashMap();
        Test<? super Number> test = new Test<>();
        test.set(new Integer(6));
        Number number = (Number) test.get();
        testHashMap.getWildcard(test);*/

        /*KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(256);
        SecretKey secretKey = keyGenerator.generateKey();
        byte[] encoded = secretKey.getEncoded();

        SecretKeySpec aes = new SecretKeySpec(encoded, "AES");

        Cipher instance = Cipher.getInstance("AES/CBC/PKCS5Padding");
        instance.init(Cipher.ENCRYPT_MODE, aes);
        byte[] bytes = instance.doFinal("aaa".getBytes());*/

        //        customScrollView.smoothScrollTo(100, 100);
        //        MyDatabase.getDatabase().getUserEntityDao().addUser(new UserEntity("陈绍鹏", 27));

        /*GroupChatEntity groupChatEntity = new GroupChatEntity();
        groupChatEntity.setName("113");
        groupChatEntity.setCreatedTime(System.currentTimeMillis());
        groupChatEntity.setLastModifiedTime(System.currentTimeMillis());
        groupChatEntity.setNeedConfirm(true);
        groupChatEntity.setCount(30);

        MyDatabase.getDatabase().getGroupChatDao().add(groupChatEntity);

        GroupChatToServerEntity groupChatToServerEntity = new GroupChatToServerEntity();
        groupChatToServerEntity.setGroupChatId(
                MyDatabase.getDatabase().getGroupChatDao().getId(groupChatEntity.getName()));
        groupChatToServerEntity.setServerId(113);

        MyDatabase.getDatabase().getGroupChatToServerDao().add(groupChatToServerEntity);*/

        /* GroupChatEntity groupChatInfo;
                List<GroupChatEntity> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {

                    groupChatInfo = new GroupChatEntity();
                    groupChatInfo.set_id(-20 - i);

                    groupChatInfo.setNeedConfirm(true);
                    groupChatInfo.setName("112-113");
                    groupChatInfo.setCount(11);
                    groupChatInfo.setLastModifiedTime(System.currentTimeMillis());
                    list.add(groupChatInfo);
                    LogUtils.d(
                            "onClick: id=" + MyDatabase.getDatabase().getGroupChatDao().add(groupChatInfo));
        //            LogUtils.d(
        //            "onClick: id=" + groupChatInfo.get_id());
                }

                LogUtils.d("onClick: " + MyDatabase.getDatabase().getGroupChatDao().getAllGroupChatInfo());*/

        /*Animator circularReveal =
                ViewAnimationUtils.createCircularReveal(customScrollView, 50, 50, 50, 100);
        circularReveal.setDuration(2000).start();*/

//        customScrollView.smoothScrollTo(100, 100);

        /*BadgeDrawable badgeDrawable = BadgeDrawable.create(this);
        badgeDrawable.setBackgroundColor(getColor(R.color.red));
        badgeDrawable.setVisible(true);
        badgeDrawable.setBadgeGravity(BadgeDrawable.TOP_START);
        badgeDrawable.setHorizontalOffset(20);
        BadgeUtils.attachBadgeDrawable(
                badgeDrawable, testNotification, (FrameLayout) testNotification.getParent());*/

        ImageView imageView = findViewById(R.id.img);

        //        FileManagerUtils.saveNetImageUrlToGallery(
        //                "https://www.baidu.com/img/PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");

        //                .into(imageView);
        //

        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 计算中心点（这里是使用view的中心作为旋转的中心点）
                        final float centerX = v.getWidth() / 2.0f;
                        final float centerY = v.getHeight() / 2.0f;

                        // 括号内参数分别为（上下文，开始角度，结束角度，x轴中心点，y轴中心点，深度，是否扭曲）
                        final Rotate3dAnimation rotation =
                                new Rotate3dAnimation(
                                        MainActivity.this, 0, 360, centerX, centerY, 0f, true, 2);

                        rotation.setDuration(3000); // 设置动画时长
                        rotation.setFillAfter(true); // 保持旋转后效果
                        rotation.setInterpolator(new LinearInterpolator()); // 设置插值器
                        v.startAnimation(rotation);
                    }
                });

        /*LruCache;
        DiskLruCache;
        DiskLruCacheFactory;
        LruCache;
        HashMap;*/

        //        Log.i("tag", AssistUtil.analysisPacketInfo());

        /*BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(getResources(), R.drawable.animated_gif_0, options);
        options.inSampleSize =
                calculateInSampleSize(options, imageView.getWidth(), imageView.getHeight());

        options.inJustDecodeBounds = false;
        Bitmap bitmap =
                BitmapFactory.decodeResource(getResources(), R.drawable.animated_gif_0, options);

        imageView.setImageBitmap(bitmap);*/

        //        LogUtils.i("onClick:"+Runtime.getRuntime().addShutdownHook(););

        //        Bitmap.Config.HARDWARE

        //        AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES);
        //        getDelegate().setLocalNightMode(MODE_NIGHT_YES);
        //        getDelegate().applyDayNight();

        /*LogUtils.d("EXTERNAL_STORAGE_PATH=" + FilePathConfig.EXTERNAL_STORAGE_PATH);
        LogUtils.d("DATA_PATH=" + FilePathConfig.DATA_PATH);
        LogUtils.d("CONTEXT_DATA_PATH=" + FilePathConfig.CONTEXT_DATA_PATH);
        LogUtils.d("CONTEXT_CACHE_PATH=" + FilePathConfig.CONTEXT_CACHE_PATH);
        LogUtils.d("CONTEXT_FILES_PATH=" + FilePathConfig.CONTEXT_FILES_PATH);
        LogUtils.d("CONTEXT_OBB_PATH=" + FilePathConfig.CONTEXT_OBB_PATH);
        LogUtils.d(
                "CONTEXT_EXTERNAL_PICTURES_PATH=" + FilePathConfig.CONTEXT_EXTERNAL_PICTURES_PATH);

        Matrix matrix = new Matrix(new Matrix());
        LogUtils.d("onClick: " + matrix.toString());
        matrix.set(new Matrix());
        //        matrix.setValues(new float[]{});
        //        matrix.mapPoints(new float[]{});
        matrix.mapRadius(5);*/

        /*Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setClassName(
                PackageInfoConfig.WECHAT_PACKAGE_NAME, "com.tencent.mm.ui.tools.AddFavoriteUI");
        intent.setType("audio/*");
        File file = new File(APP_AUDIO + File.separator + "test.mp3");
        Uri uri = UriUtils.file2Uri(file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.putExtra("tag", "录音");
        // 设置弹出框标题
        startActivity(Intent.createChooser(intent, "请选择"));*/
        //        System.exit(0);
        //        LogUtils.d("onClick: "+AppUtils.getAppsInfo());

        /*if (audioRecord == null) {

            audioRecord =
                    new AudioRecord(
                            MediaRecorder.AudioSource.MIC,
                            44100,
                            AudioFormat.CHANNEL_IN_MONO,
                            ENCODING_PCM_16BIT,
                            AudioRecord.getMinBufferSize(
                                            44100,
                                            AudioFormat.CHANNEL_IN_MONO,
                                            ENCODING_PCM_16BIT)
                                    * 10);
            LogUtils.d("onClick: " + audioRecord.getState());
        }*/
        //        LogUtils.d("onClick: " + waysToChange(614));

        /*new Thread(
                        () -> {
                            File file =
                                    new File(
                                            FilePathConfig.EXTERNAL_STORAGE_PATH
                                                    + File.separator
                                                    + "Pictures"
                                                    + File.separator
                                                    + "WeiXin"
                                                    + File.separator
                                                    + "mmexport1602225268177.jpg");
                            LogUtils.d(
                                    "解析二维码: "
                                            + QRCodeDecoderUtils.syncDecodeQRCode(
                                                    file.getAbsolutePath()));
                        })
                .start();*/
    }

    public int waysToChange(int n) {
        if (n < 5 && n > 0) {
            return 1;
        } else if (n == 0) {
            return 1;
        } else if (n < 0) {
            return 0;
        } else {

            return waysToChange(n - 25)
                    + waysToChange(n - 10)
                    - waysToChange(n - 35)
                    + waysToChange(n - 5)
                    - waysToChange(n - 15)
                    - waysToChange(n - 30)
                    + waysToChange(n - 40)
                    + 1;
        }
    }

    AudioRecord audioRecord;

    /**
     * Check by origin apk package name boolean.
     *
     * @param context the context
     * @param pkg the pkg
     * @return the boolean
     */
    public boolean checkByOriginApkPackageName(Context context, String pkg) {
        try {

            if (context == null) return false;
            int count = 0;
            //            String packageName = context.getPackageName();
            PackageManager pm = context.getPackageManager();
            List<PackageInfo> pkgs = pm.getInstalledPackages(0);
            for (PackageInfo info : pkgs) {
                if (pkg.equals(info.packageName)) {
                    count++;
                }
            }
            return count > 1;
        } catch (Exception ignore) {
        }
        return false;
    }

    /**
     * ①获取位图原尺寸 ②获取ImageView即最终图片显示的尺寸 ③依据两种尺寸计算采样率（或缩放比例）。
     *
     * @param options the options
     * @param reqWidth the req width
     * @param reqHeight the req height
     * @return the int
     */
    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 位图的原宽高通过options对象获取
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            // 当要显示的目标大小和图像的实际大小比较接近时，会产生没必要的采样，先除以2再判断以防止过度采样
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /**
     * 依据上面的最佳采样率计算方法，进一步可以封装出利用最佳采样率创建子采样版本再创建位图对象的方法，这里以从项目图片资源文件加载Bitmap对象为例：
     *
     * @param res the res
     * @param resId the res id
     * @param reqWidth the req width
     * @param reqHeight the req height
     * @return the bitmap
     */
    public static Bitmap decodeSampledBitmapFromResource(
            Resources res, int resId, int reqWidth, int reqHeight) {

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 因为inJustDecodeBounds为true，所以不会创建Bitmap对象只会扫描轮廓从而给options对象的宽高属性赋值
        BitmapFactory.decodeResource(res, resId, options);

        // 计算最佳采样率
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 记得将inJustDecodeBounds属性设置回false值
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(res, resId, options);
    }

    public static class TestStatic {
        public static int a;

        static {
            if (Looper.myLooper() != Looper.getMainLooper()) {
                LogUtils.d("static initializer: other thread");
            } else {
                LogUtils.d("static initializer: main thread");
            }
            //            LogUtils.d("static initializer: thread=" + Looper.myLooper().getThread());
            a = 1;
        }
    }

    private String sign(String pro) {

        return "";
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        //        newConfig.orientation;
        super.onConfigurationChanged(newConfig);
    }

    public class TestHashMap extends Test<String> {

        public String get() {
            return null;
        }

        public void set(String s) {}

        public Test<?> getWildcard(Test<?> wildcard) {
            wildcard.get();
            return wildcard;
        }
    }

    public static class Test<T> {

        T[] arrays;

        public static int var = 0;

        public static void test() {}

        public T get() {
            return null;
        }

        public void set(T t) {}
    }

    public void testStream() {
        List<String> teamIndia = Arrays.asList("Virat", "Dhoni", "Jadeja");
        List<String> teamAustralia = Arrays.asList("Warner", "Watson", "Smith");
        List<String> teamEngland = Arrays.asList("Alex", "Bell", "Broad");
        List<String> teamNewZeland = Arrays.asList("Kane", "Nathan", "Vettori");
        List<String> teamSouthAfrica = Arrays.asList("AB", "Amla", "Faf");
        List<String> teamWestIndies = Arrays.asList("Sammy", "Gayle", "Narine");
        List<String> teamSriLanka = Arrays.asList("Mahela", "Sanga", "Dilshan");
        List<String> teamPakistan = Arrays.asList("Misbah", "Afridi", "Shehzad");

        List<List<String>> playersInWorldCup2016 = new ArrayList<>();
        playersInWorldCup2016.add(teamIndia);
        playersInWorldCup2016.add(teamAustralia);
        playersInWorldCup2016.add(teamEngland);
        playersInWorldCup2016.add(teamNewZeland);
        playersInWorldCup2016.add(teamSouthAfrica);
        playersInWorldCup2016.add(teamWestIndies);
        playersInWorldCup2016.add(teamSriLanka);
        playersInWorldCup2016.add(teamPakistan);

        // Let's print all players before Java 8
        List<String> listOfAllPlayers = new ArrayList<>();

        for (List<String> team : playersInWorldCup2016) {
            listOfAllPlayers.addAll(team);
        }

        System.out.println("Players playing in world cup 2016");
        System.out.println(listOfAllPlayers);

        // Now let's do this in Java 8 using FlatMap
        List<String> flatMapList =
                playersInWorldCup2016.stream()
                        .flatMap((Function<List<String>, Stream<String>>) Collection::stream)
                        .collect(Collectors.toList());
        System.out.println("List of all Players using Java 8");
        System.out.println(flatMapList);

        Stream<List<String>> stream = playersInWorldCup2016.stream();

        Stream<String> streamFlatMap =
                playersInWorldCup2016.stream()
                        .flatMap(
                                new Function<List<String>, Stream<String>>() {
                                    @Override
                                    public Stream<String> apply(List<String> strings) {
                                        return strings.stream();
                                    }
                                });

        Stream<Stream<String>> streamMap =
                playersInWorldCup2016.stream()
                        .map(
                                new Function<List<String>, Stream<String>>() {
                                    @Override
                                    public Stream<String> apply(List<String> strings) {
                                        return strings.stream();
                                    }
                                });
        Stream<String> stringStream =
                playersInWorldCup2016.stream()
                        .map(
                                new Function<List<String>, Stream<String>>() {
                                    @Override
                                    public Stream<String> apply(List<String> strings) {
                                        return strings.stream();
                                    }
                                })
                        .map(
                                new Function<Stream<String>, String>() {
                                    @Override
                                    public String apply(Stream<String> stringStream) {
                                        return null;
                                    }
                                });
    }

    public interface A {
        void testA();
    }

    public class E {

        public void testA() {}
    }

    public class F extends E implements A {}

    public class B<T> implements A {
        T[] a;

        private int bInt = 3;

        @Override
        public void testA() {}
    }

    public class C extends B implements A {
        public int cInt = 1;

        @Override
        public void testA() {
            super.bInt = cInt;
            LogUtils.d("testA: " + super.bInt);
        }
    }

    public class D extends B {

        public List<?> test() {
            return new ArrayList<>();
        }
    }

    private boolean hasInterface(A b) {
        LogUtils.d("hasInterface:" + b.getClass().getName() + " ===========================");
        Class<?>[] interfaces = b.getClass().getInterfaces();
        for (Class<?> c : interfaces) {
            LogUtils.d("hasInterface: " + c.getName());
            /*if (c.equals(A.class)) {
                return true;
            }*/
        }
        return false;
    }

    /**
     * 等待控件加载
     *
     * @param id the id
     * @param time the time
     * @return the accessibility node info
     */
    public static AccessibilityNodeInfo waitNodeLoading(String id, int time)
            throws InterruptedException {
        AccessibilityNodeInfo nodeInfo;
        int count = 0;
        while (count < time) {
            nodeInfo = AssistUtil.getFirstNodeInfoByViewId(id);
            count++;
            if (nodeInfo != null && nodeInfo.isVisibleToUser()) {
                return nodeInfo;
            }
            sleep(ONE_SECOND);
        }
        return null;
    }

    String lastInfo = "";

    void printNode() {
        AccessibilityNodeInfo rootNode = AssistUtil.getRootNode();
        if (rootNode != null) {
            AccessibilityNodeInfo topmessage =
                    AssistUtil.getFirstNodeInfoByViewId(
                            "com.taobao.taobao:id/taolive_topmessage_view1", rootNode);

            AccessibilityNodeInfo taoliveChatItemContent =
                    AssistUtil.getNodeInfoByParentAndChild(topmessage, 0, 0, 1);

            StringBuilder stringBuilder = new StringBuilder();
            AssistUtil.analysisPacketInfo(taoliveChatItemContent, stringBuilder);
            LogUtils.i("printNode: " + stringBuilder.toString());

            if (!StringUtils.isTrimEmpty(getNodeInfoTextByNode(taoliveChatItemContent))
                    && !lastInfo.equals(getNodeInfoTextByNode(taoliveChatItemContent))) {
                lastInfo = getNodeInfoTextByNode(taoliveChatItemContent);
                LogUtils.d("lastInfo: " + lastInfo);
            }

            if (taoliveChatItemContent != null) {
                taoliveChatItemContent.refresh();
                taoliveChatItemContent.recycle();
                taoliveChatItemContent = null;
            }

            if (topmessage != null) {
                topmessage.refresh();
                topmessage.recycle();
                topmessage = null;
            }

            if (rootNode != null) {
                rootNode.refresh();
                rootNode.recycle();
                rootNode = null;
            }
        }
    }

    /**
     * 提取字符串中的数字（价格）
     *
     * @param price the price
     * @return the price
     */
    public List<Double> getPrice(String price) {
        //        boolean startMatchFirst = false;
        //        boolean endMatchFirst = false;
        price = price + " ";
        List<Double> doubles = new ArrayList<>();
        StringBuilder priceBuilder = new StringBuilder();
        for (int i = 0, l = price.length(); i < l; i++) {
            if (price.charAt(i) >= 48 && price.charAt(i) <= 57) {
                //                startMatchFirst = true;
                //                if (!endMatchFirst) {
                //                    start.append(price.charAt(i));
                priceBuilder.append(price.charAt(i));
                //                } else {
                //                    end.append(price.charAt(i));
                //                }
            } else if (price.charAt(i) == 46) {
                //                if (!endMatchFirst) {
                priceBuilder.append(price.charAt(i));
                //                } else {
                //                    end.append(price.charAt(i));
                //                }
            } else {
                try {
                    doubles.add(Double.parseDouble(priceBuilder.toString()));
                    priceBuilder = new StringBuilder();
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return doubles;
    }

    private Handler handlerInterval =
            new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    LogUtils.d(timeList.get(0));
                    timeList.remove(0);
                    timeInterval.remove(0);
                    count--;
                    if (count > 0) {
                        handlerInterval.postDelayed(null, timeInterval.get(0));
                    }
                }
            };

    /** @return 用户安装的应用 */
    private Map<String, String> getApps() {
        Map<String, String> apps = new HashMap<>();
        PackageManager pm = getPackageManager();
        List<PackageInfo> packs = pm.getInstalledPackages(0);

        for (PackageInfo pi : packs) {
            // 显示用户安装的应用程序，而不显示系统程序
            if ((pi.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0
                    && (pi.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                // 应用程序名称
                apps.put(
                        pi.applicationInfo.packageName,
                        pi.applicationInfo.loadLabel(pm).toString());
            }
        }
        return apps;
    }

    public static boolean scrollForward(AccessibilityNodeInfo scrollNode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return nodeDispatchGestureInfoScrollForward(scrollNode, 500);
        } else {
            return nodePerformInfoScrollForward(scrollNode);
        }
    }

    public static void scrollForward(float fromX, float fromY, float toX, float toY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(fromX, fromY, toX, toY, 500);
        }
    }

    public static void scrollBackward(AccessibilityNodeInfo scrollNode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScrollBackward(scrollNode, 500);
        } else {
            nodePerformInfoScrollBackward(scrollNode);
        }
    }

    NotificationBroadcastReceiver notificationBroadcastReceiver;
    public static HandlerThread handlerThread;
    public static Handler handler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LogUtils.d("onCreate: App 启动");
        /*if (!AccessibilityCheckUtils.isAccessibilityEnable(
                getApplicationContext(),
                getPackageName() + "/" + AutoScriptService.class.getCanonicalName())) {

            LogUtils.d("onCreate: 暂无无障碍权限，请授权");
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                LogUtils.d("onCreate: 暂无悬浮窗权限，请授权");

            } else if (!CustomAppUtlis.isServiceRunning(
                    MyApplication.getContext(), FloatWindowService.class.getName())) {
                startService(new Intent(MainActivity.this, FloatWindowService.class));
            } else {
            }
        } else {
        }*/

        handlerThread = new HandlerThread("taskThread");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());

        broadCast = new MyBroadCast();
        printViewBroadCast = new PrintViewBroadCast();
        clickNodeBroadCast = new clickNodeBroadCast();

        IntentFilter filter = new IntentFilter();
        filter.addAction("com.csp.test");
        filter.setPriority(Integer.MAX_VALUE);
        registerReceiver(broadCast, filter);

        IntentFilter printFilter = new IntentFilter();
        printFilter.addAction("com.csp.print");
        printFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(printViewBroadCast, printFilter);

        IntentFilter clickFilter = new IntentFilter();
        clickFilter.addAction("com.csp.click");
        clickFilter.setPriority(Integer.MAX_VALUE);
        registerReceiver(clickNodeBroadCast, clickFilter);

        notificationBroadcastReceiver = new NotificationBroadcastReceiver();
        registerBroadcastReceiver(
                notificationBroadcastReceiver,
                "com.timemm.permission.SEND_PUSH_NOTIFICATION",
                ACTION_CLICK,
                ACTION_CANCEL);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollForward() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(
                    InitData.getInstance().getScreenWidth() >> 1,
                    (InitData.getInstance().getScreenHeight() * 3) >> 2,
                    InitData.getInstance().getScreenWidth() >> 1,
                    InitData.getInstance().getScreenHeight() >> 2,
                    500);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollBackward() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(
                    InitData.getInstance().getScreenWidth() >> 1,
                    InitData.getInstance().getScreenHeight() >> 2,
                    InitData.getInstance().getScreenWidth() >> 1,
                    (InitData.getInstance().getScreenHeight() * 3) >> 2,
                    500);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadCast);
        unregisterReceiver(printViewBroadCast);
        unregisterReceiver(clickNodeBroadCast);
        unregisterBroadcastReceiver(notificationBroadcastReceiver);
        //        unbindService(serviceConnection);
        //        stopService(new Intent(MainActivity.this, FloatWindowService.class));
    }

    @Override
    protected void initData() {

        /*Intent startAutoScriptService = new Intent();
        startAutoScriptService.setClass(getApplicationContext(), AutoScriptService.class);
        startService(startAutoScriptService);*/
    }

    @Override
    protected void initView() {

        countdownView.setTotalTime(2);
        boolean asIsEnable =
                AccessibilityCheckUtils.isAccessibilityEnable(
                        getApplicationContext(),
                        this.getPackageName() + "/" + AutoScriptService.class.getCanonicalName());
        asState.setText("" + asIsEnable);
        if (asIsEnable) {
            asState.setTextColor(getColor(R.color.green));
        } else {
            asState.setTextColor(getColor(R.color.red));
        }

        testNotification.setOnClickListener(
                v -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        String channelId = "chat";
                        String channelName = "聊天消息";
                        int importance = NotificationManager.IMPORTANCE_HIGH;
                        createNotificationChannel(channelId, channelName, importance);

                        channelId = "subscribe";
                        channelName = "订阅消息";
                        importance = NotificationManager.IMPORTANCE_DEFAULT;
                        createNotificationChannel(channelId, channelName, importance);

                        channelId = "recommend";
                        channelName = "推荐消息";
                        importance = NotificationManager.IMPORTANCE_DEFAULT;
                        createNotificationChannel(channelId, channelName, importance);
                    }
                    //                    ActivityUtils.startActivity(NotificationActivity.class);
                    setContentView(R.layout.activity_main);
                });

        searchEdit.setOnTextChangedListener(
                new SearchEdit.OnTextChangedListener() {
                    @Override
                    public void onTextChanged(String text) {
                        if (searchText != null) {
                            searchText.setText(text);
                        }
                    }
                });

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        Log.d("Shutdown", "Shutdown");
                                    }
                                }));

        /*goodsSortView.setOnItemClickListener(
        new GoodsSortView.onItemClickListener() {
            @Override
            public void onClick(int state) {
                */
        /*ToastUtils.showShort("state=" + state);
        startFloatingService();*/
        /*
                Intent intent =
                        new Intent(
                                "android.intent.action.VIEW",
                                Uri.parse("csp://csp:8080/mytest?query1=1&query2=true"));
                ActivityUtils.startActivity(WebActivity.class);
            }
        });*/
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    class clickNodeBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            // 点击控件
            boolean longClick = intent.getBooleanExtra("longClick", false);
            String nodeId = intent.getStringExtra("id");
            String nodeText = intent.getStringExtra("text");
            String coordinateText = intent.getStringExtra("coordinate");
            boolean gesture = intent.getBooleanExtra("gesture", false);
            //            LogUtils.d("id:" + nodeId);
            //            LogUtils.d("text:" + nodeText);
            //            LogUtils.d("coordinateText:" + coordinateText);

            if (longClick) {
                if (!StringUtils.isTrimEmpty(nodeId)) {
                    AccessibilityNodeInfo idNode = AssistUtil.getFirstNodeInfoByViewId(nodeId);
                    if (idNode != null) {
                        LogUtils.d("onReceive: " + idNode.toString());
                        if (gesture) {
                            AssistUtil.nodeDispatchGestureInfoLongClick(idNode, 500);
                        } else {
                            AssistUtil.nodePerformInfoLongClick(idNode);
                        }
                    }
                } else if (!StringUtils.isTrimEmpty(nodeText)) {
                    AccessibilityNodeInfo textNode = AssistUtil.getFirstNodeInfoByText(nodeText);
                    if (textNode != null) {
                        LogUtils.d("onReceive: " + textNode.toString());
                        AssistUtil.nodePerformInfoLongClick(textNode);
                    }
                }
            } else {
                if (!StringUtils.isTrimEmpty(nodeId)) {
                    List<AccessibilityNodeInfo> idNodeList =
                            AssistUtil.getNodeInfosByViewId(nodeId);
                    if (idNodeList != null && idNodeList.size() > 0) {
                        for (AccessibilityNodeInfo idNode : idNodeList) {
                            LogUtils.d(idNode.getText());
                        }
                        LogUtils.d("onReceive: " + idNodeList.get(0).toString());
                        if (gesture) {
                            AssistUtil.nodeDispatchGestureInfoClick(idNodeList.get(0));
                        } else {

                            AssistUtil.nodePerformInfoClick(idNodeList.get(0));
                        }
                    }

                    /*AccessibilityNodeInfo idNode = AssistUtil.getFirstNodeInfoByViewId(nodeId);
                    if (idNode != null) {
                        LogUtils.d("onReceive: " + idNode.toString());
                        AssistUtil.nodePerformInfoClick(idNode);
                    }*/
                } else if (!StringUtils.isTrimEmpty(nodeText)) {
                    AccessibilityNodeInfo textNode =
                            AssistUtil.getFirstExactNodeInfoByText(nodeText);
                    if (textNode != null) {
                        AssistUtil.nodePerformInfoClick(textNode);
                    }

                    /*AccessibilityNodeInfo textNode = AssistUtil.getFirstNodeInfoByText(nodeText);
                    if (textNode != null) {
                        LogUtils.d("onReceive: " + textNode.toString());
                        AssistUtil.nodePerformInfoClick(textNode);
                    }*/
                } else if (!StringUtils.isTrimEmpty(coordinateText)) {
                    String[] split = coordinateText.split(",");
                    try {
                        int x = Integer.parseInt(split[0]);
                        int y = Integer.parseInt(split[1]);
                        nodeDispatchGestureInfoClick(x, y);
                    } catch (Exception e) {
                        LogUtils.e("onReceive: ", e);
                    }
                }
            }
        }
    }

    class PrintViewBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            //            nodeDispatchGestureInfoClick(rectAttend);
            LogUtils.i("遍历节点广播~");
            LogUtils.d("onReceive:ScreenWidth= " + ScreenUtils.getScreenWidth());
            LogUtils.d("onReceive:ScreenHeight= " + ScreenUtils.getScreenHeight());
            LogUtils.i(AssistUtil.getCurrentAPPPackageName());
            if (!AccessibilityCheckUtils.isAccessibilityEnable(
                    getApplicationContext(),
                    context.getPackageName() + "/" + AutoScriptService.class.getCanonicalName())) {
                intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, REQUEST_ACCESSIBILITY_CODE);
            } else {
                // 打印节点信息
                AccessibilityNodeInfo nodeInfo;
                String nodeId = intent.getStringExtra("id");
                if (nodeId == null) {
                    nodeInfo = AssistUtil.getRootNode();
                } else {
                    nodeInfo = AssistUtil.getFirstNodeInfoByViewId(nodeId);
                }
                StringBuilder stringBuilder = new StringBuilder();
                AssistUtil.analysisPacketInfo(nodeInfo, stringBuilder);
                LogUtils.d(stringBuilder);

                /*AccessibilityNodeInfo nodeInfoByParentAndChild =
                AssistUtil.getNodeInfoByParentAndChild(
                        nodeInfo, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2,
                        3);*/
                /*AccessibilityNodeInfo nodeInfoByParentAndChild =
                        AssistUtil.getFirstNodeInfoByText("反馈", nodeInfo);
                LogUtils.i("onReceive: " + nodeInfoByParentAndChild);

                AssistUtil.nodePerformInfoClick(nodeInfoByParentAndChild);*/
                /*LogUtils.file(
                "====================================\n"
                        + stringBuilder
                        + "====================================\n");*/
            }

            //            LogUtils.i(new Person("柯基", "10", new Address("浙江", "杭州", "西湖区")));
        }
    }

    /**
     * 在指定节点下寻找可以滚动的所有节点 Scroll.
     *
     * @param info the info
     */
    public void scroll(AccessibilityNodeInfo info) {
        if (info != null) {
            boolean state = AssistUtil.nodePerformInfoScrollForward(info);
            //            boolean state = scrollForward(info);
            if (state) {
                LogUtils.d(info.toString());
            }
            sleep(100L);
            int count = info.getChildCount();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    scroll(info.getChild(i));
                }
            }
        }
    }

    //    /**
    //     * 在指定节点下寻找可以滚动的所有节点 Scroll.
    //     *
    //     * @param info the info
    //     */
    //    public List<AccessibilityNodeInfo> getNodeByWidgetName(
    //            AccessibilityNodeInfo info, String widget) {
    //        List<AccessibilityNodeInfo> nodeInfoList = new ArrayList<>();
    //        if (info == null || StringUtils.isTrimEmpty(widget)) return nodeInfoList;
    //        int count = info.getChildCount();
    //        if (count > 0) {
    //            for (int i = 0; i < count; i++) {
    //                scroll(info.getChild(i));
    //            }
    //        }
    //    }

    /**
     * 在指定节点下寻找可以滚动的所有节点 Scroll.
     *
     * @param info the info
     */
    public AccessibilityNodeInfo getNodeByWidgetName(AccessibilityNodeInfo info, String widget) {
        AccessibilityNodeInfo nodeInfo;
        if (info == null || StringUtils.isTrimEmpty(widget)) return null;
        int count = info.getChildCount();
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                if (info.getChild(i).getClassName().toString().contains(widget)) {
                    return info.getChild(i);
                } else {
                    nodeInfo = getNodeByWidgetName(info.getChild(i), widget);
                    if (nodeInfo != null) {
                        return nodeInfo;
                    }
                }
            }
        }
        return null;
    }

    private void throwException(int i) throws NullPointerException, IndexOutOfBoundsException {
        switch (i) {
            case 1:
                throw new NullPointerException();
            case 2:
                throw new IndexOutOfBoundsException();
            default:
                LogUtils.d("throwException: " + i);
        }
    }

    public static String sortString(String string) {
        char[] chars = string.replaceAll(" ", "").toCharArray();
        Arrays.sort(chars);
        String sortString = new String(chars);
        LogUtils.d("sortString: " + sortString);
        return sortString;
    }

    Rect rectAttend = new Rect();

    String ooo000OOOooo = "嗯";
    String ooo00OOOoo = "啊";
    String ooo0OOOoo0O = "哦";

    static Object objStatic = new Object();
    Object obj = new Object();

    void change() {
        objStatic = obj;
    }

    class MyBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //            ScreenShotUtils.getInstance().requestCapturePermission();
            LogUtils.i("脚本广播~");
            int screenW = ScreenUtils.getScreenWidth();
            int screenH = ScreenUtils.getScreenHeight();
            if (!AccessibilityCheckUtils.isAccessibilityEnable(
                    getApplicationContext(),
                    context.getPackageName() + "/" + AutoScriptService.class.getCanonicalName())) {
                intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                startActivityForResult(intent, 11);
            } else {
                if (!isServiceRunning(
                        context, "com.example.aidltest.FloatWindowService")) {
                    //                    startFloatingService();
                }

                Intent finalIntent = intent;

                new Thread(
                                () -> {
                                    //
                                    // ClipboardUtils.copyTextAsync("₴WjpNc1MaUmo₴");
                                    //                                    AccessibilityNodeInfo
                                    // title =
                                    //
                                    // AssistUtil.getFirstExactNodeInfoByText("@苏宁官旗");
                                    //
                                    //
                                    //
                                    // AssistUtil.nodeDispatchGestureInfoClick(
                                    //
                                    //
                                    // title.getParent().getParent().getChild(1));

                                    //
                                    // AssistUtil.nodeDispatchGestureInfoClick(
                                    //
                                    // ScreenUtils.getScreenWidth() >> 1,
                                    //
                                    // ScreenUtils.getScreenHeight() >> 1);
                                    //                                    sleep(250);
                                    //
                                    // AssistUtil.nodeDispatchGestureInfoClick(
                                    //
                                    // ScreenUtils.getScreenWidth() >> 1,
                                    //
                                    // ScreenUtils.getScreenHeight() >> 1);

                                    /*Intent SearchDoor =
                                            new Intent("com.taobao.tao.search.SearchDoor");
                                    //        Intent intent = new Intent();
                                    SearchDoor.setClassName(
                                            PackageInfoConfig.TAOBAO_PACKAGE_NAME,
                                            "com.taobao.search.searchdoor.SearchDoorActivity");
                                    AssistUtil.gotoTargetActivity(SearchDoor);
                                    LogFileUtils.fileTask("gotoTBSearch", "gotoTBSearch");*/
                                    //
                                    // choosePropertyVer286ByRandom(false);

                                    // 所有窗口
                                    List<AccessibilityWindowInfo> windows =
                                            assistService.getWindows();
                                    for (int i = 0, l = windows.size(); i < l; i++) {
                                        LogUtils.d("onReceive: " + windows.get(i).toString());
                                        StringBuilder stringBuilder1 = new StringBuilder();
                                        AssistUtil.analysisPacketInfo(
                                                windows.get(i).getRoot(), stringBuilder1);
                                        LogUtils.d("getRoot: " + stringBuilder1.toString());
                                    }

                                    /*AccessibilityNodeInfo list =
                                            AssistUtil.getNodeByWidgetName("ListView");
                                    AssistUtil.nodePerformInfoScrollForward(list);*/

                                    /*ScreenShotUtils.getInstance()
                                    .beginScreenShot(
                                            "test",
                                            new ScreenShotUtils.Callback() {
                                                @Override
                                                public void getPath(String path) {
                                                    LogUtils.d("getPath: " + path);
                                                }
                                            });*/

                                    /*AssistUtil.nodePerformInfoClick(
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.TAODETAIL_PUBLIC_MENU));
                                    sleep(ONE_SECOND << 1);
                                    AssistUtil.nodePerformInfoClick(
                                            AssistUtil.getFirstExactNodeInfoByText("分享"));
                                    sleep(ONE_SECOND);
                                    try {
                                        waitNodeLoading(TaoBWidgetId.TAODETAIL_TV_CHANNEL_NAME, 60);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    sleep(ONE_SECOND);
                                    AssistUtil.nodePerformInfoClick(
                                            AssistUtil.getFirstExactNodeInfoByText("复制链接"));
                                    sleep(ONE_SECOND);
                                    AssistUtil.nodePerformInfoClick(
                                            AssistUtil.getFirstExactNodeInfoByText("取消"));*/

                                    /*Bitmap bitmap = ScreenUtils.screenShot(MainActivity.this);
                                    ImageUtils.save(
                                            bitmap,
                                            FilePathConfig.APP_PATH
                                                    + File.separator
                                                    + ("screen_shot"
                                                            + System.currentTimeMillis()
                                                            + ".jpg"),
                                            Bitmap.CompressFormat.JPEG);*/

                                    // MediaExtractor
                                    /*MediaExtractor extractor = new MediaExtractor();
                                    try {
                                        extractor.setDataSource(
                                                "rtmp://liveng.alicdn.com/mediaplatform/08fcc2b1-ee23-4c95-9aaf-06d6dcf5209b_liveng-360p-hp?auth_key=1579429472-0-0-5c9693cc5edb38fc010acba3a2f2e2c9");
                                        AudioBean audioBean = selectTrack(extractor);
                                        LogUtils.d("onReceive: " + audioBean);

                                    } catch (Exception e) {
                                        LogUtils.e("onReceive: a", e);
                                    }*/

                                    //
                                    // CustomAppUtlis.gotoTargetActivity(PackageInfoConfig.TIKTOK_PACKAGE_NAME);

                                    //
                                    // nodeDispatchGestureInfoClick(1285, 115);

                                    /*scrollForward(
                                            screenW >> 1,
                                            screenH * 0.875f,
                                            screenW >> 1,
                                            screenH * 0.125f);
                                    sleep(ONE_SECOND);*/

                                    // 跳转抖音
                                    /*PackageManager packageManager = getPackageManager();
                                    Intent intentTT = null;
                                    try {
                                        intentTT =
                                                Intent.parseUri(
                                                        "snssdk1128://aweme/detail/6701214246551784711?refer=web&gd_label=click_wap_download_banner&appParam=&needlaunchlog=1",
                                                        Intent.URI_INTENT_SCHEME);
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                    ResolveInfo info =
                                            packageManager.resolveActivity(
                                                    intentTT, PackageManager.MATCH_DEFAULT_ONLY);
                                    // 跳到该应用
                                    if (info != null) {
                                        startActivity(intentTT);
                                    }*/

                                    /*AccessibilityNodeInfo attend =
                                                                                AssistUtil.getFirstNodeInfoByViewId(
                                                                                        "com.ss.android.ugc.aweme:id/ub");
                                    //                                    nodePerformInfoClick(attend);
                                                                        attend.getBoundsInScreen(rectAttend);
                                                                        LogUtils.d("onReceive: "+rectAttend.toShortString());*/
                                    //
                                    // nodeDispatchGestureInfoClick(rectAttend);

                                    // 浏览评论
                                    /*Random random = new Random();
                                    int count = random.nextInt(3) + 3;
                                    List<String> strings = new ArrayList<>(5);
                                    strings.add("有图");
                                    strings.add("好评");
                                    strings.add("追加");
                                    strings.add("差评");
                                    strings.add("中评");
                                    Collections.shuffle(strings);
                                    LogUtils.d("onReceive: " + strings);
                                    AccessibilityNodeInfo commentNode =
                                            AssistUtil.getFirstNodeInfoByText("宝贝评价");
                                    nodePerformInfoClick(commentNode);
                                    sleep(ONE_SECOND << 1);
                                    AccessibilityNodeInfo commentDetailNode;
                                    AccessibilityNodeInfo commentRecycleViewNode;
                                    AccessibilityNodeInfo commentLabelNode;
                                    commentDetailNode =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.GOODS_COMMENT_DETAIL);
                                    commentRecycleViewNode =
                                            getNodeByWidgetName(commentDetailNode, "RecyclerView");
                                    int browseCount = 0;
                                    List<String> labelChooseList = new ArrayList<>();
                                    for (String labelText : strings) {
                                        if (commentRecycleViewNode != null) {
                                            if (AssistUtil.getFirstNodeInfoByText(
                                                            labelText,
                                                            commentRecycleViewNode.getChild(0))
                                                    != null) {
                                                LogUtils.d("choose label:" + labelText);
                                                labelChooseList.add(labelText);
                                                browseCount++;
                                                if (browseCount == count) {
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    for (int i = 0, l = labelChooseList.size(); i < l; i++) {
                                        commentLabelNode =
                                                AssistUtil.getFirstNodeInfoByText(
                                                        labelChooseList.get(i),
                                                        commentRecycleViewNode.getChild(0));
                                        nodePerformInfoClick(commentLabelNode);
                                        sleep(ONE_SECOND << 1);
                                        scrollForward(
                                                screenW >> 1,
                                                screenH * 0.875f,
                                                screenW >> 1,
                                                screenH * 0.125f);
                                        sleep(ONE_SECOND << 1);
                                        AssistUtil.performReturnBack();
                                        sleep(ONE_SECOND);
                                        if (i != l - 1) {
                                            nodePerformInfoClick(commentNode);
                                            sleep(ONE_SECOND << 1);
                                            commentDetailNode =
                                                    AssistUtil.getFirstNodeInfoByViewId(
                                                            TaoBWidgetId.GOODS_COMMENT_DETAIL);
                                            commentRecycleViewNode =
                                                    getNodeByWidgetName(commentDetailNode, "RecyclerView");

                                        }
                                    }*/

                                    /*AccessibilityNodeInfo priceNode;
                                    AccessibilityNodeInfo titleNode;
                                    AccessibilityNodeInfo addressNode;
                                    AccessibilityNodeInfo shopNode;

                                    AccessibilityNodeInfo webview =
                                            AssistUtil.getFirstNodeInfoByText("WVUCWebView");
                                    if (webview != null) {
                                        priceNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        webview, 0, 0, 0, 0, 1, 3, 0, 1);
                                        titleNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        webview, 0, 0, 0, 0, 1, 5, 0);
                                        addressNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        webview, 0, 0, 0, 0, 1, 6, 1);
                                        shopNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        webview, 0, 0, 0, 0, 1, 16, 0, 1);

                                        AccessibilityNodeInfo shareNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        webview, 0, 0, 0, 0, 1, 5, 1, 1);
                                        AssistUtil.nodePerformInfoClick(shareNode);
                                        sleep(ONE_SECOND);
                                        AccessibilityNodeInfo copyUrlNode =
                                                AssistUtil.getAfterNodeInfoByViewId(
                                                        "com.taobao.taobao:id/tv_channel_name", 0);
                                        AssistUtil.nodePerformInfoClick(copyUrlNode);
                                        sleep(ONE_SECOND);

                                    } else {
                                        AccessibilityNodeInfo main =
                                                AssistUtil.getFirstNodeInfoByViewId(
                                                        TaoBWidgetId.MAIN_PAGE);
                                        LogUtils.d("onReceive: " + main);

                                        priceNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        AssistUtil.getFirstNodeInfoByViewId(
                                                                "com.taobao.taobao:id/detail_uniform_price_lt"),
                                                        0,
                                                        0,
                                                        0);
                                        if (priceNode == null || priceNode.getText() == null) {
                                            priceNode =
                                                    AssistUtil.getFirstNodeInfoByViewId(
                                                            "com.taobao.taobao:id/tvPrice1");
                                        }
                                        if (priceNode == null || priceNode.getText() == null) {
                                            priceNode =
                                                    AssistUtil.getFirstNodeInfoByViewId(
                                                            "com.taobao.taobao:id/tv_jhs_common_price");
                                        }

                                        titleNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        main, 0, 3, 0);

                                        if (titleNode == null || titleNode.getText() == null) {
                                            titleNode =
                                                    AssistUtil.getNodeInfoByParentAndChild(
                                                            main, 0, 3, 0, 0);
                                        }
                                        if (titleNode == null || titleNode.getText() == null) {
                                            titleNode =
                                                    AssistUtil.getNodeInfoByParentAndChild(
                                                            main, 0, 3, 0, 0, 0);
                                        }
                                        if (titleNode == null || titleNode.getText() == null) {
                                            titleNode =
                                                    AssistUtil.getNodeInfoByParentAndChild(
                                                            main, 0, 3, 0, 0, 0, 0);
                                        }

                                        AccessibilityNodeInfo addressNodeText =
                                                AssistUtil.getFirstNodeInfoByText("发货");
                                        int countAddress = 0;
                                        while (addressNodeText == null && countAddress < 3) {
                                            AssistUtil.nodeDispatchGestureInfoScroll(
                                                    ScreenUtils.getScreenWidth() >> 1,
                                                    (ScreenUtils.getScreenHeight() * 3) >> 2,
                                                    ScreenUtils.getScreenWidth() >> 1,
                                                    ScreenUtils.getScreenHeight() >> 2,
                                                    500);
                                            countAddress++;
                                            sleep(ONE_SECOND);
                                            addressNodeText =
                                                    AssistUtil.getFirstNodeInfoByText("发货");
                                        }
                                        addressNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        addressNodeText, 1, 2);

                                        AccessibilityNodeInfo shopTipNode =
                                                AssistUtil.getFirstNodeInfoByText("点击可进入店铺");
                                        int countShop = 0;
                                        while (shopTipNode == null && countShop < 3) {
                                            AssistUtil.nodeDispatchGestureInfoScroll(
                                                    ScreenUtils.getScreenWidth() >> 1,
                                                    (ScreenUtils.getScreenHeight() * 3) >> 2,
                                                    ScreenUtils.getScreenWidth() >> 1,
                                                    ScreenUtils.getScreenHeight() >> 2,
                                                    500);
                                            countShop++;
                                            sleep(ONE_SECOND);
                                            shopTipNode =
                                                    AssistUtil.getFirstNodeInfoByText("点击可进入店铺");
                                        }
                                        shopNode =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        shopTipNode, 0, 1, 0);
                                    }
                                    try {
                                        LogUtils.d("价格:" + priceNode.getText().toString().trim());

                                    } catch (Exception e) {
                                        LogUtils.e(e);
                                    }
                                    try {
                                        LogUtils.d("标题:" + titleNode.getText().toString().trim());
                                    } catch (Exception e) {
                                        LogUtils.e(e);
                                    }
                                    try {
                                        LogUtils.d(
                                                "发货地:" + addressNode.getText().toString().trim());
                                    } catch (Exception e) {
                                        LogUtils.e(e);
                                    }
                                    try {
                                        LogUtils.d("店铺:" + shopNode.getText().toString().trim());
                                    } catch (Exception e) {
                                        LogUtils.e(e);
                                    }*/

                                    // 滚动
                                    /*float screenW = ScreenUtils.getScreenWidth();
                                    float screenH = ScreenUtils.getScreenHeight();
                                    AssistUtil.nodeDispatchGestureInfoScroll(
                                            screenW / 2,
                                            screenH /2,
                                            screenW / 2,
                                            screenH /4,
                                            500);*/

                                    /*AssistUtil.nodePerformInfoClick(
                                    AssistUtil.getAfterNodeInfoByViewId(
                                            TaoBWidgetId.LL_ICON, 0));*/
                                    /*LogUtils.d(
                                    "onReceive: "
                                            + TimeUtils.millis2String(
                                                    System.currentTimeMillis(),
                                                    new SimpleDateFormat(
                                                            "yyyyMMddHHmmss",
                                                            Locale.getDefault())));*/

                                    // 压缩文件
                                    /*try {
                                        List<File> zipFiles = new ArrayList<>();
                                        File zipToPath =
                                                new File(
                                                        EXTERNAL_STORAGE_PATH
                                                                + File.separator
                                                                + "flowbox"
                                                                + File.separator
                                                                + "log"
                                                                + File.separator
                                                                + "logs.zip");
                                        List<File> netFiles =
                                                FileUtils.listFilesInDir(
                                                        EXTERNAL_STORAGE_PATH
                                                                + File.separator
                                                                + "flowbox"
                                                                + File.separator
                                                                + "log"
                                                                + File.separator
                                                                + "net"
                                                                + File.separator);
                                        List<File> taskFiles =
                                                FileUtils.listFilesInDir(
                                                        EXTERNAL_STORAGE_PATH
                                                                + File.separator
                                                                + "flowbox"
                                                                + File.separator
                                                                + "log"
                                                                + File.separator
                                                                + "task"
                                                                + File.separator);
                                        if (netFiles != null && netFiles.size() > 0) {
                                            zipFiles.addAll(netFiles);
                                        }
                                        if (taskFiles != null && taskFiles.size() > 0) {
                                            zipFiles.addAll(taskFiles);
                                        }
                                        long start = System.currentTimeMillis();
                                        LogUtils.d(ZipUtils.zipFiles(zipFiles, zipToPath));
                                        LogUtils.d(
                                                (System.currentTimeMillis() - start) / 1000 + "s");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }*/

                                    /*ActivityUtils.startActivity(
                                    new Intent("com.taobao.tao.search.SearchDoor"));*/
                                    /*AssistUtil.nodeDispatchGestureInfoScroll(
                                    720, 2240, 720, 320, 500);*/

                                    /*List<AccessibilityNodeInfo> text =
                                            AssistUtil.getNodeInfosByText(
                                                    "Sony/索尼 MDR-XB80BS入耳式蓝牙耳机运动跑步重低音防水通话耳麦");
                                    LogUtils.d("onReceive: " + text.toString());*/

                                    //                                    Looper.prepare();
                                    /*ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData data = cm.getPrimaryClip();
                                    ClipData.Item item = data.getItemAt(0);
                                    String content = item.getText().toString();*/
                                    //                                    LogUtils.d("onReceive: " +
                                    // ClipboardUtils.getText());
                                    //                                    Looper.loop();

                                    //

                                    //
                                    //
                                    // scroll(AssistUtil.getRootNode());

                                    //
                                    // choosePropertyByRandom(false);

                                    // 接收Event
                                    /*sleep(1000L);
                                    EventBus.getDefault().post(new FloatEvent(true));*/

                                    // 打开淘宝
                                    /*AssistUtil.gotoTargetActivity("com.taobao.taobao");
                                    sleep(ONE_SECOND << 1);*/

                                    /*ShellUtils.CommandResult commandResult =
                                            ShellUtils.execCmd(
                                                    "dumpsys activity | findstr mFocusedActivity",
                                                    false,
                                                    true);
                                    LogUtils.d(commandResult);*/

                                    /*File screenFile =
                                            new File(
                                                    Environment.getExternalStorageDirectory()
                                                            + File.separator
                                                            + "myScreen");
                                    if (!screenFile.exists()) {
                                        screenFile.mkdirs();
                                    }
                                    LogUtils.d(
                                            ImageUtils.save(
                                                    ScreenUtils.screenShot(MainActivity.this, true),
                                                    new File(
                                                            screenFile,
                                                            System.currentTimeMillis() + ".jpg"),
                                                    Bitmap.CompressFormat.JPEG));
                                    LogUtils.d(Config.CACHE_PATH);*/

                                    /*AccessibilityNodeInfo accessibilityNodeInfo =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    SystemWidgetId.SETTING_SUB_SWITCH_WIDGET);
                                    LogUtils.i(
                                            accessibilityNodeInfo.performAction(
                                                    AccessibilityNodeInfo.ACTION_CLICK));*/

                                    // 迷之滚动
                                    /*AccessibilityNodeInfo accessibilityNodeInfo =
                                            AssistUtil.getRootNode();
                                    scroll(accessibilityNodeInfo);
                                    LogUtils.d("滚完啦？");*/

                                    // 返回首页
                                    /*while (AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.IV_BTN_BACKGROUND)
                                            == null) {
                                        AssistUtil.returnBackByAccessibly();
                                        sleep(1000L);
                                    }
                                    LogUtils.d("返回首页啦~");*/

                                    // Android7.0以上版本可以分发事件给无障碍服务，相当于直接点击屏幕中的具体坐标
                                    /*if (android.os.Build.VERSION.SDK_INT
                                            >= android.os.Build.VERSION_CODES.N) {
                                        nodeDispatchGestureInfoClick(
                                                AssistUtil.getFirstNodeInfoByViewId(
                                                        TaoBWidgetId.GOODS_IMAGE));
                                    }*/

                                    // AssistUtil.startMarket(MainActivity.this,
                                    // "com.taobao.taobao");

                                    // 打‘我的购物车’页面
                                    /*Intent intent1 = new Intent("android.intent.action.VIEW");
                                    intent1.addCategory("android.intent.category.DEFAULT");
                                    intent1.setData(
                                            Uri.parse("http://newcart.taobao.com/my_cart.htm"));
                                    AssistUtil.gotoTargetActivity(intent1);
                                    sleep(2000L);
                                    nodePerformInfoClick(AssistUtil.getFirstNodeInfoByText("手机淘宝"));
                                    sleep(1000L);
                                    nodePerformInfoClick(
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    SystemWidgetId.BUTTON_ALWAYS));*/
                                    // 购物车数量
                                    /*AccessibilityNodeInfo cartNum = AssistUtil.getFirstNodeInfoByViewId(
                                            TaoBWidgetId.SHOPPING_CART_NUM);
                                    LogUtils.d(cartNum.getText());*/
                                    // 删除购物车商品
                                    /*AccessibilityNodeInfo manage = AssistUtil.getFirstNodeInfoByViewId(
                                            TaoBWidgetId.SHOPPING_CART_GOODS_NAME);
                                    nodePerformInfoLongClick(manage);
                                    sleep(1000L);
                                    AccessibilityNodeInfo delete = AssistUtil.getFirstNodeInfoByViewId(
                                            TaoBWidgetId.SHOPPING_CART_DIALOG_DELETE);
                                    nodePerformInfoClick(delete);*/

                                    // 打开‘我的淘宝’页面
                                    /*Intent intent1 = new Intent("android.intent.action.VIEW");
                                    intent1.addCategory("android.intent.category.BROWSABLE");
                                    intent1.setData(
                                            Uri.parse("http://my.m.taobao.com/myTaobao.htm"));
                                    AssistUtil.gotoTargetActivity(intent1);
                                    sleep(2000L);
                                    nodePerformInfoClick(AssistUtil.getFirstNodeInfoByText("手机淘宝"));
                                    sleep(2000L);
                                    nodePerformInfoClick(
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    SystemWidgetId.BUTTON_ALWAYS));
                                    sleep(2000L);
                                    nodePerformInfoClick(
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.MYTAOBAO_SETTING));
                                    sleep(4000L);*/

                                    // 收藏夹数量
                                    /*AccessibilityNodeInfo coNum = AssistUtil.getFirstNodeInfoByViewId(TaoBWidgetId
                                            .MYTAOBAO_LISTVIEW).getChild(2).getChild(0).getChild(0)
                                            .getChild(0).getChild(0);
                                    LogUtils.d(coNum.getText());*/
                                    // 跳转收藏夹
                                    //                                    List<String> gList = new
                                    // ArrayList<>();
                                    //
                                    // gList.add("容声小太阳取暖器家用立式电热扇烤火炉暖风机学生…");
                                    //                                    gList.add("出售二手正版任天堂
                                    // Switch NS游戏  任天…");
                                    //                                    gList.add("三星/Samsung
                                    // 970PRO EVO固态硬盘512GB  M…");
                                    //                                    gList.add("Intel酷睿I7 9700K
                                    // 散片 技嘉/微星 MPG AORUS…");
                                    //
                                    // gList.add("奥克斯小太阳取暖器家用节能电暖气省电暖风机浴室…");
                                    //
                                    // gList.add("日系休闲连帽秋季男夹克外套新款帅气潮流嘻哈宽松…");
                                    //                                    gList.add("王储
                                    // ssd固态盘1t台式机笔记本2.5英寸固态硬…");
                                    //
                                    // gList.add("27寸曲面144hz电竞屏2K高清IPS液晶LED显示器24…");
                                    //                                    AccessibilityNodeInfo
                                    // collect =
                                    //
                                    // AssistUtil.getFirstNodeInfoByText("收藏夹");
                                    //
                                    // nodePerformInfoClick(collect);
                                    //                                    sleep(ONE_SECOND << 2);
                                    //                                    //
                                    // 删除收藏的商品（只能根据商品名搜索，该页面是weex找不到控件ID）
                                    //                                    AccessibilityNodeInfo
                                    // manage =
                                    //
                                    // AssistUtil.getFirstNodeInfoByText("管理");
                                    //
                                    // nodePerformInfoClick(manage);
                                    //                                    sleep(ONE_SECOND);
                                    //
                                    // deleteGoodsByCollection(gList);
                                    //                                    sleep(ONE_SECOND);
                                    //                                    AccessibilityNodeInfo
                                    // delete =
                                    //
                                    // AssistUtil.getFirstNodeInfoByText("删除");
                                    //
                                    // nodePerformInfoClick(delete);

                                    // 跳转搜索页面
                                    /*Intent intent1 = new Intent("com.taobao.tao.search.SearchDoor");
                                    AssistUtil.gotoTargetActivity(intent1);
                                    sleep(ONE_SECOND << 2);

                                    AccessibilityNodeInfo edit =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.SEARCH_EDIT);
                                    AssistUtil.setEditText(edit, "固态硬盘");
                                    sleep(2000L);
                                    AccessibilityNodeInfo search =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.SEARCH_BTN);
                                    nodePerformInfoClick(search);
                                    sleep(4000L);*/

                                    // 淘口令跳转
                                    //                                    ClipboardUtils.copyText(
                                    //
                                    // "【红心取暖器家用居浴室电暖器炉立式办公室电暖气片节能省电暖风机】https://m.tb.cn/h.3rGevUi?sm=92ab4c 点击链接，再选择浏览器咑閞；或復·制这段描述￥B9gobK5PJtd￥后到淘♂寳♀");
                                    //                                    sleep(1000L);
                                    //
                                    // AppUtils.launchApp("com.taobao.taobao");
                                    //                                    sleep(4000L);
                                    //
                                    // AssistUtil.nodePerformInfoClick(
                                    //
                                    // AssistUtil.getFirstNodeInfoByViewId(
                                    //
                                    // TaoBWidgetId.WORD_ITEM_ACTION));

                                    /*AccessibilityNodeInfo list =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.GOODS_LIST);
                                    AssistUtil.nodeDispatchGestureInfoScrollForward(list, 500);*/
                                    // 点击商品详情
                                    /*AccessibilityNodeInfo list =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.GOODS_LIST);
                                    AssistUtil.nodePerformInfoScrollForward(list);
                                    sleep(2000L);
                                    AssistUtil.nodePerformInfoScrollForward(list);
                                    sleep(2000L);
                                    List<AccessibilityNodeInfo> itemList =
                                            AssistUtil.getNodeInfosByViewId(
                                                    TaoBWidgetId.GOODS_MORE_FUNC_BTN);
                                    LogUtils.d(itemList);
                                    AssistUtil.nodePerformInfoClick(
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.GOODS_MORE_FUNC_BTN));
                                    sleep(4000L);
                                    AssistUtil.nodePerformInfoClick(
                                            AssistUtil.getFirstNodeInfoByText("去购买"));
                                    sleep(4000L);*/

                                    // 浏览商品
                                    /*for (int i = 0; i < 6; i++) {
                                        LogUtils.d(
                                                AssistUtil.nodeDispatchGestureInfoScrollForward(
                                                        AssistUtil.getFirstNodeInfoByViewId(
                                                                        TaoBWidgetId.PAGE_CONTENT)
                                                                .getChild(0),
                                                        400));
                                        sleep(1000L);
                                    }*/
                                    /*AccessibilityNodeInfo mainPage =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.MAIN_PAGE);
                                    for (int i = 0, l = 3; i < l; i++) {
                                        LogUtils.d(
                                                AssistUtil.nodePerformInfoScrollForward(mainPage));
                                        sleep(1000L);
                                    }
                                    nodePerformInfoClick(
                                            AssistUtil.getAfterNodeInfoByViewId(
                                                    TAODETAIL_NAV_BAR_TAB, 2));
                                    sleep(1000L);
                                    AccessibilityNodeInfo webView =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.DETAIL_WEB_VIEW);
                                    AccessibilityNodeInfo scrollView = null;
                                    if (webView != null) {
                                        scrollView =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        webView, 0, 0, 0);
                                    } else {
                                        scrollView =
                                                AssistUtil.getNodeInfoByParentAndChild(
                                                        mainPage, 1, 2, 0, 0);
                                    }
                                    for (int i = 0, l = 5; i < l; i++) {
                                        LogUtils.d(
                                                AssistUtil.nodePerformInfoScrollForward(
                                                        scrollView));
                                        sleep(1000L);
                                    }*/

                                    // 商品收藏
                                    /*AssistUtil.nodePerformInfoClick(
                                    AssistUtil.getFirstNodeInfoByViewId(
                                            TaoBWidgetId.FAV_CONTAINER));*/

                                    //
                                    // AssistUtil.nodePerformInfoClick(
                                    //
                                    // AssistUtil.getNodeInfosByViewId(TaoBWidgetId.LL_ICON)
                                    //                                                    .get(0));
                                    //                                    sleep(4000L);
                                    //
                                    //
                                    // nodePerformInfoClick(AssistUtil.getFirstNodeInfoByText("关注"));

                                    // 分享
                                    /*AccessibilityNodeInfo goods =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.MAINPAGE);
                                    sleep(1000L);
                                    AccessibilityNodeInfo share =
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                            TaoBWidgetId.MAINPAGE)
                                                    .getChild(3)
                                                    .getChild(1)
                                                    .getChild(1);
                                    if (share.getText().toString().contains("分享")) {
                                        nodePerformInfoClick(share);
                                        sleep(1000L);
                                        nodePerformInfoClick(
                                                AssistUtil.getFirstNodeInfoByViewId(
                                                        TaoBWidgetId.SHARE_SAVE_IMG));
                                        sleep(1000L);
                                        nodePerformInfoClick(
                                                AssistUtil.getFirstNodeInfoByText("QQ"));
                                        LogUtils.d(System.currentTimeMillis());
                                    }*/

                                    // 加入购物车
                                    /*nodePerformInfoClick(
                                            AssistUtil.getNodeInfosByViewId(
                                                            TaoBWidgetId.DETAIL_MAIN_SYS_BUTTON)
                                                    .get(0));
                                    sleep(1000L);
                                    List<String> propertyList = new ArrayList<>();
                                    propertyList.add("860 EVO 1TB 1TB SATA 2.5英寸");
                                    propertyList.add("增值服务");
                                    List<AccessibilityNodeInfo> nodeInfoList =
                                            AssistUtil.getNodeInfosByViewId(
                                                    TaoBWidgetId.PROPERTY_DESC);
                                    LogUtils.i(chooseProperty(propertyList, nodeInfoList));
                                    sleep(1000L);
                                    nodePerformInfoClick(
                                            AssistUtil.getFirstNodeInfoByViewId(
                                                    TaoBWidgetId.CONFIRM));*/

                                    // 店铺搜索按钮
                                    /*AccessibilityNodeInfo sou = AssistUtil.getFirstNodeInfoByText("搜索");
                                    nodePerformInfoClick(sou);*/

                                    /*nodePerformInfoClick(
                                    AssistUtil.getFirstNodeInfoByViewId(
                                            TaoBWidgetId.MYTAOBAO_SETTING));*/

                                    // 复制
                                    /*handler.post(
                                    new Runnable() {
                                        @Override
                                        public void run() {
                                            ClipboardUtils.copyText(
                                                    "【立即领券】复制$adLh1ZiBgJI$打开手机淘宝领券并下单");
                                        }
                                    });*/

                                    //
                                    // ClipboardUtils.copyText("http://v.douyin.com/SBHsc5/");

                                    ClipboardUtils.getTextAsync(
                                            new ClipboardUtils.Callback<CharSequence>() {
                                                @Override
                                                public void call(CharSequence text) {
                                                    LogUtils.d(ClipboardUtils.getText());
                                                }
                                            });

                                    try {
                                        LogUtils.d(
                                                "Apk Info: "
                                                        + AppUtils.getAppInfo(
                                                                CURRENT_PACKAGE.get()));
                                    } catch (Exception e) {
                                        LogUtils.e("onReceive: ", e);
                                        e.printStackTrace();
                                    }

                                    while (true) {
                                        AssistUtil.nodeDispatchGestureInfoClick(
                                                ScreenUtils.getScreenWidth() >> 1,
                                                ScreenUtils.getScreenHeight() >> 1);
                                        sleep(300);
                                    }
                                })
                        .start();
            }
        }

        /*
         * 适配淘宝286（9.7.2）购物车变动
         * */
        private void choosePropertyVer286ByRandom(boolean isRandom) {
            AccessibilityNodeInfo choiceTextNode = AssistUtil.getFirstNodeInfoByText("已选:");
            if (choiceTextNode != null) {
                LogUtils.i(
                        "choosePropertyVer286ByRandom: "
                                + AssistUtil.getNodeInfoTextByNode(choiceTextNode));
            } else {
                choiceTextNode = AssistUtil.getFirstNodeInfoByText("请选择");
                if (choiceTextNode != null) {
                    String choiceText = AssistUtil.getNodeInfoTextByNode(choiceTextNode);
                    LogUtils.i("choosePropertyByRandom: " + choiceText);
                    String[] pros = choiceText.split(" ");
                    // 要选择的商品属性列表
                    Queue<String> proList = new LinkedList<>(Arrays.asList(pros));
                    if (CollectionUtils.isNotEmpty(proList)) {
                        proList.poll();

                        AccessibilityNodeInfo firstNodeInfoByViewId =
                                AssistUtil.getFirstNodeInfoByViewId("com.taobao.taobao:id/body");

                        AccessibilityNodeInfo proListNode = null;

                        for (int i = 0, l = firstNodeInfoByViewId.getChildCount(); i < l; i++) {
                            if (AssistUtil.getFirstNodeInfoByText(
                                            proList.peek(), firstNodeInfoByViewId.getChild(i))
                                    != null) {
                                proListNode = firstNodeInfoByViewId.getChild(i);
                                break;
                            }
                        }

                        if (proListNode != null && proListNode.getChildCount() > 0) {

                            String pro = proList.poll();
                            boolean matchPro = false;

                            AccessibilityNodeInfo childItem;

                            for (int i = 0, l = proListNode.getChildCount(); i < l; i++) {

                                if (matchPro) {
                                    childItem = proListNode.getChild(i);
                                    if (childItem != null && childItem.getChildCount() > 0) {

                                        List<AccessibilityNodeInfo> chooseItems =
                                                AssistUtil.getNodeInfosByText("可选", childItem);

                                        AccessibilityNodeInfo chooseItem = null;

                                        for (int j = 0, jl = chooseItems.size(); j < jl; j++) {
                                            if (!AssistUtil.getNodeInfoTextByNode(
                                                            chooseItems.get(j))
                                                    .contains("不可选")) {
                                                chooseItem = chooseItems.get(j);
                                                break;
                                            }
                                        }

                                        AssistUtil.nodePerformInfoClick(chooseItem);

                                        LogUtils.i(
                                                "choosePropertyByRandom: "
                                                        + getNodeInfoTextByNode(chooseItem));
                                        sleep(ONE_SECOND);

                                        pro = proList.poll();

                                        if (pro == null) {
                                            break;
                                        }

                                        matchPro = false;
                                        continue;
                                    }
                                }

                                if (AssistUtil.getNodeInfoTextByNode(proListNode.getChild(i))
                                        .contains(pro)) {
                                    matchPro = true;
                                    continue;
                                }
                            }
                        }
                    }
                }
            }

            AssistUtil.nodeDispatchGestureInfoClick(
                    AssistUtil.findLastNodeInGivenParent(
                            AssistUtil.getFirstNodeInfoByViewId("com.taobao.taobao:id/footer")));
        }

        public void deleteGoodsByCollection(List<String> goodsList) {
            int choiceGoodsList = 0;
            AccessibilityNodeInfo weex =
                    AssistUtil.getFirstNodeInfoByViewId(TaoBWidgetId.FAVORITE_WEEX_RENDER_VIEW);
            AccessibilityNodeInfo recycleView =
                    AssistUtil.getNodeInfoByParentAndChild(
                            weex, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1);
            AccessibilityNodeInfo recycleViewTwo =
                    AssistUtil.getNodeInfoByParentAndChild(weex, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0);
            AccessibilityNodeInfo textNode;
            Iterator<String> iterator = goodsList.iterator();
            String goods;
            int goodsSize = goodsList.size();
            while (choiceGoodsList < goodsSize) {
                if (recycleView != null && recycleView.getChildCount() > 0) {
                    for (int i = 0, l = recycleView.getChildCount(); i < l; i++) {
                        textNode =
                                AssistUtil.getNodeInfoByParentAndChild(
                                        recycleView.getChild(i), 0, 0, 0, 0, 2, 0);
                        if (textNode != null && textNode.getContentDescription() != null) {
                            for (iterator = goodsList.iterator(); iterator.hasNext(); ) {
                                goods = iterator.next();
                                LogUtils.i("deleteGoodsByCollection: " + goods);
                                if (goods.contains(
                                        textNode.getContentDescription()
                                                .toString()
                                                .substring(
                                                        0,
                                                        textNode.getContentDescription()
                                                                        .toString()
                                                                        .length()
                                                                - 3))) {
                                    nodePerformInfoClick(textNode);
                                    iterator.remove();
                                    sleep(ONE_SECOND);
                                    choiceGoodsList++;
                                    break;
                                }
                            }
                        }
                        /*sleep(ONE_SECOND);
                        AccessibilityNodeInfo delete = AssistUtil.getFirstNodeInfoByText("删除");
                        nodePerformInfoClick(delete);*/
                    }
                }
                if (recycleViewTwo != null && recycleViewTwo.isVisibleToUser()) {
                    LogUtils.i("deleteGoodsByCollection: 滑到底啦~");
                    return;
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        nodeDispatchGestureInfoScrollForward(recycleView, 500);
                    } else {
                        nodePerformInfoScrollForward(recycleView);
                    }
                    sleep(ONE_SECOND);
                    weex =
                            AssistUtil.getFirstNodeInfoByViewId(
                                    TaoBWidgetId.FAVORITE_WEEX_RENDER_VIEW);
                    recycleView =
                            AssistUtil.getNodeInfoByParentAndChild(
                                    weex, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 1);
                    recycleViewTwo =
                            AssistUtil.getNodeInfoByParentAndChild(
                                    weex, 0, 0, 0, 1, 0, 0, 0, 0, 0, 1, 0);
                }
            }
        }

        /** 选择商品属性<br> */
        private void choosePropertyModifiedByRandom(boolean isRandom) throws InterruptedException {
            AccessibilityNodeInfo choiceTextNode =
                    AssistUtil.getFirstNodeInfoByViewId(TaoBWidgetId.TAOSKU_CHOOSED);
            int count = 0;
            while (count < 30 && choiceTextNode == null) {
                count++;
                sleep(ONE_SECOND);
                choiceTextNode = AssistUtil.getFirstNodeInfoByViewId(TaoBWidgetId.TAOSKU_CHOOSED);
            }
            if (choiceTextNode == null) {
                //                LogFileUtils.fileTask("choosePropertyByRandom", "load fail");
                return;
            }
            if (choiceTextNode.getText() != null) {
                if (choiceTextNode.getText().toString().contains("已选:")) {
                    //                    LogFileUtils.fileTask("choosePropertyByRandom", "choose
                    // taosku skip");
                    LogUtils.i("choosePropertyByRandom: 已选");
                } else {
                    String choiceText = choiceTextNode.getText().toString();
                    LogUtils.i("choosePropertyByRandom: " + choiceText);
                    //                    LogFileUtils.fileTask(
                    //                            "choosePropertyByRandom", "choose taosku original
                    // text:" + choiceText);
                    int choiceTextIndex = choiceText.indexOf("选择");
                    String propertys = choiceText.substring(choiceTextIndex + 3);
                    String[] pros;
                    /*todo “,”分组或“ ”分组*/
                    if (propertys.contains(",")) {
                        pros = propertys.split(",");
                    } else if (propertys.contains(" ")) {
                        pros = propertys.split(" ");
                    } else {
                        pros = propertys.split(",");
                    }

                    // 要选择的商品属性列表
                    List<String> stringList = new ArrayList<>(Arrays.asList(pros));
                    String pro;
                    LogUtils.i("choosePropertyByRandom: " + Arrays.toString(pros));
                    //                    LogFileUtils.fileTask(
                    //                            "choosePropertyByRandom", "choose taosku:" +
                    // Arrays.toString(pros));
                    // 获取商品属性块控件
                    AccessibilityNodeInfo proContent =
                            AssistUtil.getFirstNodeInfoByViewId(
                                    TaoBWidgetId.TAOSKU_NATIVE_VIEW_LAYOUT);

                    // 某一个属性下可选择的具体属性项列表
                    List<AccessibilityNodeInfo> proNodes;
                    // 商品属性控件列表键值对
                    Map<String, List<AccessibilityNodeInfo>> propertyMap = new HashMap<>();
                    Map<String, Integer> propertyIndexMap = new HashMap<>();
                    Map<String, Boolean> propertyChoose = new HashMap<>();

                    if (proContent != null && proContent.getChildCount() > 0) {
                        // 子控件的大小就是可选择的属性的大小
                        for (int i = 0, l = proContent.getChildCount(); i < l; i++) {
                            for (int j = 0, k = stringList.size(); j < k; j++) {
                                pro = stringList.get(j);
                                if (AssistUtil.getFirstNodeInfoByText(pro, proContent.getChild(i))
                                        != null) {

                                    proNodes =
                                            getNodeInfosByViewId(
                                                    TaoBWidgetId.TAOSKU_PROPERTY_DESC,
                                                    proContent.getChild(i));
                                    if (proNodes != null && proNodes.size() > 0) {
                                        propertyMap.put(pro, proNodes);
                                        propertyIndexMap.put(pro, 0);
                                        propertyChoose.put(pro, true);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    Integer index;
                    String property;
                    if (ObjectUtils.isNotEmpty(propertyMap)) {

                        boolean select = false;

                        do {
                            for (int i = 0, l = stringList.size(); i < l; i++) {

                                property = stringList.get(i);
                                proNodes = propertyMap.get(property);
                                index = propertyIndexMap.get(property);

                                if (ObjectUtils.isEmpty(proNodes) || index == null) {
                                    continue;
                                }

                                if (isRandom) {

                                } else {
                                    for (int j = index, k = proNodes.size(); j < k; j++) {
                                        // 如果当前选中的属性为“不可选择”状态，则继续选下一个具体属性
                                        if (getNodeInfoTextByNode(
                                                        proNodes.get(j).getParent().getParent())
                                                .contains("不可选择")) {
                                            continue;
                                        }

                                        if (propertyChoose.get(property)) {
                                            // 选择具体属性
                                            nodePerformInfoClick(proNodes.get(j));
                                            LogUtils.i(
                                                    "choosePropertyByRandom: "
                                                            + getNodeInfoTextByNode(
                                                                    proNodes.get(j)));
                                            propertyChoose.put(property, false);
                                        }

                                        //                                    LogFileUtils.fileTask(
                                        //
                                        // "choosePropertyByRandom",
                                        //                                            "choose
                                        // definite
                                        // taosku:"
                                        //                                                    +
                                        // property
                                        //                                                    + "-"
                                        //                                                    +
                                        // getNodeInfoTextByNode(proNodes.get(j)));
                                        // 等待进度条加载
                                        if (waitLoadProgress(
                                                TaoBWidgetId.TAODETAIL_PROGRESS_TEXT, 30)) {
                                            sleep(ONE_SECOND);
                                            if (AssistUtil.getFirstNodeInfoByViewId(
                                                            TaoBWidgetId.TAODETAIL_REFRESH)
                                                    != null) {
                                                // 点击重试按钮
                                                nodePerformInfoClick(
                                                        AssistUtil.getFirstNodeInfoByViewId(
                                                                TaoBWidgetId.TAODETAIL_REFRESH));
                                            } else {
                                                sleep(ONE_SECOND);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }

                            if (!"确定"
                                    .equals(
                                            AssistUtil.getNodeInfoTextByNode(
                                                    AssistUtil.getFirstNodeInfoByViewId(
                                                            TaoBWidgetId.TAOSKU_CONFIRM_TEXT)))) {

                                for (int i = 0, l = stringList.size(); i < l; i++) {

                                    property = stringList.get(i);
                                    proNodes = propertyMap.get(property);
                                    index = propertyIndexMap.get(property);

                                    if (ObjectUtils.isEmpty(proNodes) || index == null) {
                                        continue;
                                    }
                                    index++;
                                    propertyChoose.put(property, true);
                                    if (index >= proNodes.size()) {
                                        index = 0;
                                        propertyIndexMap.put(property, index);
                                    } else {
                                        propertyIndexMap.put(property, index);
                                        break;
                                    }
                                }

                            } else {
                                select = true;
                            }

                        } while (!select);
                    }
                }
            }
        }

        public void choosePropertyByRandom(boolean isRandom) {
            AccessibilityNodeInfo choiceTextNode =
                    AssistUtil.getFirstNodeInfoByViewId(TaoBWidgetId.TAOSKU_CHOOSED);
            if (choiceTextNode != null && choiceTextNode.getText() != null) {
                if (choiceTextNode.getText().toString().contains("已选:")) {
                    LogUtils.i("choosePropertyByRandom: 已选");
                } else {
                    String choiceText = choiceTextNode.getText().toString();
                    int choiceTextIndex = choiceText.indexOf("选择");
                    String[] pros = choiceText.substring(choiceTextIndex + 3).split(",");
                    List<String> stringList = new ArrayList<>(Arrays.asList(pros));
                    Iterator<String> iterator;
                    String pro;
                    LogUtils.i("choosePropertyByRandom: " + Arrays.toString(pros));
                    AccessibilityNodeInfo proContent =
                            AssistUtil.getFirstNodeInfoByViewId(
                                    TaoBWidgetId.TAOSKU_NATIVE_VIEW_LAYOUT);
                    List<AccessibilityNodeInfo> proNodes;
                    if (proContent != null && proContent.getChildCount() > 0) {
                        for (int i = 0, l = proContent.getChildCount(); i < l; i++) {
                            for (iterator = stringList.iterator(); iterator.hasNext(); ) {
                                pro = iterator.next();
                                if (AssistUtil.getFirstNodeInfoByText(pro, proContent.getChild(i))
                                        != null) {
                                    proNodes =
                                            getNodeInfosByViewId(
                                                    TaoBWidgetId.TAOSKU_PROPERTY_DESC,
                                                    proContent.getChild(i));
                                    if (proNodes != null && proNodes.size() > 0) {
                                        if (isRandom) {
                                        } else {
                                            for (int j = 0, k = proNodes.size(); j < k; j++) {
                                                if (proNodes.get(j).getParent().getParent() != null
                                                        && proNodes.get(j)
                                                                        .getParent()
                                                                        .getParent()
                                                                        .getContentDescription()
                                                                != null
                                                        && proNodes.get(j)
                                                                .getParent()
                                                                .getParent()
                                                                .getContentDescription()
                                                                .toString()
                                                                .contains("不可选择")) {
                                                    continue;
                                                }
                                                nodePerformInfoClick(proNodes.get(j));
                                                if (loadProgress(
                                                        TaoBWidgetId.TAODETAIL_PROGRESS_TEXT, 30)) {
                                                    sleep(ONE_SECOND);
                                                    if (AssistUtil.getFirstNodeInfoByViewId(
                                                                    TaoBWidgetId.TAODETAIL_REFRESH)
                                                            != null) {
                                                        nodePerformInfoClick(
                                                                AssistUtil.getFirstNodeInfoByViewId(
                                                                        TaoBWidgetId
                                                                                .TAODETAIL_REFRESH));
                                                    } else {
                                                        iterator.remove();
                                                        sleep(ONE_SECOND);
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        /**
         * Choose property boolean.
         *
         * @param propertyList the property list
         * @return the boolean
         */
        public boolean chooseProperty(List<String> propertyList) {
            List<AccessibilityNodeInfo> nodeInfoList =
                    AssistUtil.getNodeInfosByViewId(TaoBWidgetId.TAOSKU_PROPERTY_DESC);
            if (nodeInfoList != null && nodeInfoList.size() > 0) {
                for (String property : propertyList) {
                    for (int i = 0, l = nodeInfoList.size(); i < l; i++) {
                        if (nodeInfoList.get(i).getText() != null
                                && property.equals(nodeInfoList.get(i).getText().toString())) {
                            AccessibilityNodeInfo nodeInfoPro = nodeInfoList.get(i);
                            while (!nodeInfoPro.isVisibleToUser()) {
                                nodePerformInfoScrollForward(
                                        AssistUtil.getFirstNodeInfoByViewId(
                                                TaoBWidgetId.TAOSKU_SCROLLVIEW));
                                sleep(1000L);
                                List<AccessibilityNodeInfo> afterScrollNodeList =
                                        AssistUtil.getNodeInfosByViewId(
                                                TaoBWidgetId.TAOSKU_PROPERTY_DESC);
                                if (afterScrollNodeList != null && afterScrollNodeList.size() > 0) {
                                    nodeInfoPro = afterScrollNodeList.get(i);
                                } else {
                                    return false;
                                }
                            }
                            nodePerformInfoClick(nodeInfoList.get(i));
                            sleep(1000L);
                            nodeInfoList =
                                    AssistUtil.getNodeInfosByViewId(
                                            TaoBWidgetId.TAOSKU_PROPERTY_DESC);
                            if (nodeInfoList == null || nodeInfoList.size() <= 0) {
                                return false;
                            }
                            break;
                        }
                    }
                }
                return true;
            }
            return false;
        }

        /**
         * 等待进度条加载
         *
         * @param time 等待时间
         * @return 是否超时，false,超时，true,未超时
         */
        public boolean waitLoadProgress(String viewId, int time) throws InterruptedException {
            int count = 0;
            AccessibilityNodeInfo progress = getLoadProgressById(viewId);
            while (count < time && progress != null && progress.isVisibleToUser()) {
                sleep(ONE_SECOND);
                count++;
                progress = getLoadProgressById(viewId);
            }
            if (count == time) {
                return false;
            }
            sleep(ONE_SECOND);
            LogUtils.d("load Progress success,time:" + count);
            return true;
        }

        private AccessibilityNodeInfo getLoadProgressById(String viewId) {
            AccessibilityNodeInfo rootNode = getRootNode();
            if (rootNode == null) {
                return null;
            }
            List<AccessibilityNodeInfo> nodeInfos =
                    rootNode.findAccessibilityNodeInfosByViewId(viewId);
            return nodeInfos.size() > 0 ? nodeInfos.get(0) : null;
        }
    }

    ServiceConnection serviceConnection =
            new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    LogUtils.d(name.toShortString() + "onServiceConnected");
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    LogUtils.d(name.toShortString() + "onServiceDisconnected");
                }

                @Override
                public void onBindingDied(ComponentName name) {
                    LogUtils.d(name.toShortString() + "onBindingDied");
                }

                @Override
                public void onNullBinding(ComponentName name) {
                    LogUtils.d(name.toShortString() + "onNullBinding");
                }
            };

    public static boolean startFloatingService(
            Activity context, ServiceConnection serviceConnection) {
        if (!AccessibilityCheckUtils.isAccessibilityEnable(
                context,
                context.getPackageName() + "/" + AutoScriptService.class.getCanonicalName())) {
            Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
            context.startActivityForResult(intent, REQUEST_ACCESSIBILITY_CODE);
            return false;
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(context)) {
                ToastUtils.showShort("当前无权限，请授权");
                context.startActivityForResult(
                        new Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:" + context.getPackageName())),
                        REQUEST_FLOATING_CODE);
                return false;
            } else {
                Intent intent = new Intent(context, FloatWindowService.class);
                context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
//                context.startService(intent);
                return true;
                /*else if (!CustomAppUtlis.isServiceRunning(
                        MyApplication.getContext(), FloatWindowService.class.getName())) {

                    return true;
                } else {
                    return false;
                }*/
            }
        } else {
            return false;
        }
    }

    /*
     * 判断服务是否启动,context上下文对象 ，className服务的name
     */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager =
                (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList =
                activityManager.getRunningServices(30);

        if (!(serviceList.size() > 0)) {
            return false;
        }

        for (int i = 0; i < serviceList.size(); i++) {
            //            LogUtils.d(serviceList.get(i).service.getClassName());
            if (serviceList.get(i).service.getClassName().equals(className)) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 等待进度条加载
     *
     * @param time 等待时间
     * @return 是否超时，false,超时，true,未超时
     */
    protected boolean loadProgress(String id, int time) {
        int count = 0;
        AccessibilityNodeInfo progress = getLoadProgress(id);
        while (count < time && progress != null && progress.isVisibleToUser()) {
            sleep(ONE_SECOND);
            count++;
            progress = getLoadProgress(id);
        }
        if (count == time) {
            return false;
        }
        sleep(ONE_SECOND);
        return true;
    }

    /**
     * 获取视频文件信息
     *
     * @param extractor extractor
     */
    private AudioBean selectTrack(MediaExtractor extractor) {
        int numTracks = extractor.getTrackCount();
        AudioBean audioBean = new AudioBean();
        for (int i = 0; i < numTracks; i++) {
            MediaFormat format = extractor.getTrackFormat(i);
            String mime = format.getString(MediaFormat.KEY_MIME);
            if (mime.startsWith("audio/")) {
                audioBean.setAudioTrackMime(mime);
                //                audioTrackMime = mime;
            } else if (mime.startsWith("video/")) {
                audioBean.setVideoWidth(format.getInteger(MediaFormat.KEY_WIDTH));
                audioBean.setVideoHeight(format.getInteger(MediaFormat.KEY_HEIGHT));
                audioBean.setVideoTrackMime(mime);
                audioBean.setDuration(format.getLong(MediaFormat.KEY_DURATION));
                //                videoWidth = format.getInteger(MediaFormat.KEY_WIDTH);
                //                videoHeight = format.getInteger(MediaFormat.KEY_HEIGHT);
                //                videoTrackMime = mime;
                //                duration = format.getLong(MediaFormat.KEY_DURATION);
            }
            LogUtils.d("Extractor selected track " + i + " (" + mime + "): " + format);
        }
        return audioBean;
    }

    public static class AudioBean {
        /** 视频轨道类型 */
        private String videoTrackMime = "";

        /** 音频轨道类型 */
        private String audioTrackMime = "";

        /** 视频宽度 */
        private int videoWidth;

        /** 视频高度 */
        private int videoHeight;

        /** 视频长度 */
        private long duration;

        public AudioBean() {}

        public String getVideoTrackMime() {
            return videoTrackMime;
        }

        public void setVideoTrackMime(String videoTrackMime) {
            this.videoTrackMime = videoTrackMime;
        }

        public String getAudioTrackMime() {
            return audioTrackMime;
        }

        public void setAudioTrackMime(String audioTrackMime) {
            this.audioTrackMime = audioTrackMime;
        }

        public int getVideoWidth() {
            return videoWidth;
        }

        public void setVideoWidth(int videoWidth) {
            this.videoWidth = videoWidth;
        }

        public int getVideoHeight() {
            return videoHeight;
        }

        public void setVideoHeight(int videoHeight) {
            this.videoHeight = videoHeight;
        }

        public long getDuration() {
            return duration;
        }

        public void setDuration(long duration) {
            this.duration = duration;
        }

        @Override
        public String toString() {
            return "AudioBean{"
                    + "videoTrackMime='"
                    + videoTrackMime
                    + '\''
                    + ", audioTrackMime='"
                    + audioTrackMime
                    + '\''
                    + ", videoWidth="
                    + videoWidth
                    + ", videoHeight="
                    + videoHeight
                    + ", duration="
                    + duration
                    + '}';
        }
    }

    public void inspect(List<? super String> list) {
        for (Object obj : list) {
            System.out.println(obj);
        }
        list.add(""); // 这个操作在当前方法的上下文是合法的。
    }

    public void test() {
        List<String> strs = new ArrayList<String>();
        inspect(strs); // 编译错误
    }

    private AccessibilityNodeInfo getLoadProgress(String viewId) {
        AccessibilityNodeInfo rootNode = getRootNode();
        if (rootNode == null) {
            return null;
        }
        List<AccessibilityNodeInfo> nodeInfos = rootNode.findAccessibilityNodeInfosByViewId(viewId);
        return nodeInfos.size() > 0 ? nodeInfos.get(0) : null;
    }

    /**
     * 当fragment进行切换时，采用隐藏与显示的方法加载fragment以防止数据的重复加载
     *
     * <p>要替换的fragment
     */
    /*private void switchContent(Fragment to) {

        if (currentFragment != to) {

            //            FragmentManager fm = getSupportFragmentManager();
            //            FragmentTransaction ft = fm.beginTransaction();

            // 添加渐隐渐现的动画
            // 隐藏当前的fragment
            if (currentFragment != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .hide(currentFragment)
                        .commitAllowingStateLoss();
            }
            // 先判断是否被add过
            if (!to.isAdded()) {
                getSupportFragmentManager().beginTransaction().remove(to).commitAllowingStateLoss();
                // add下一个到Activity中
                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.app_content, to)
                        .commitAllowingStateLoss();
            } else {
                // 显示下一个
                getSupportFragmentManager().beginTransaction().show(to).commitAllowingStateLoss();
            }
            currentFragment = to;
        }
    }*/

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    boolean isRecord;
    short[] shorts = new short[2048];

    @OnClick(R.id.test_lottery)
    public void testLotteryClick() {

        startFloatingService(this, serviceConnection);

        /*if (!isRecord) {
         */
        /*isRecord = true;
        audioRecord.startRecording();

        audioRecord.read(shorts, 0, 2048);*/
        /*

            AudioRecordUtils.getInstance(false).recordChat("test.mp3");
            //            AudioRecordUtils.getInstance(false).prepare();
        } else {

            AudioRecordUtils.getInstance(false).stopRecord();
            */
        /*audioRecord.stop();
        try {
            writeShorts(shorts, "test.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        /*
        }*/
        /*if (startFloatingService()) {

            ThreadUtils.executeByFixed(
                    1,
                    new ThreadUtils.SimpleTask<Object>() {
                        @Nullable
                        @Override
                        public Object doInBackground() throws Throwable {
                            countDown(10);
                            FloatWindowService.isRun.getAndSet(true);
                            ToastUtils.showShort("开始");
                            ThreadUtils.executeByFixed(
                                    1,
                                    new ThreadUtils.SimpleTask<Object>() {
                                        @Nullable
                                        @Override
                                        public Object doInBackground() throws Throwable {
                                            Random random = new Random();

                                            while (FloatWindowService.isRun.get()) {
                                                sleep((random.nextInt(6) + 5) * 1000L);
                                                AssistUtil.nodeDispatchGestureInfoClick(718, 1580);
                                                sleep(2000L);
                                                AssistUtil.nodeDispatchGestureInfoClick(1108, 115);
                                                sleep((random.nextInt(16) + 20) * 1000L);
                                                checkEnd();
                                            }

                                            return null;
                                        }

                                        @Override
                                        public void onSuccess(@Nullable Object result) {
                                            ToastUtils.showShort("结束");
                                        }
                                    });

                            return null;
                        }

                        @Override
                        public void onSuccess(@Nullable Object result) {}
                    });
        }*/
    }

    private static void writeShorts(short[] shorts, String filename) throws IOException {
        File file = createFile(APP_AUDIO, filename);
        FileOutputStream fos = new FileOutputStream(file);
        for (short aShort : shorts) {
            fos.write(toBytes(aShort));
        }
        fos.close();
    }

    private static File createFile(String path, String filename) throws IOException {
        File pathFile = new File(path);
        if (!pathFile.exists()) {
            pathFile.mkdirs();
        }

        File file = new File(pathFile, filename);
        if (!file.exists()) {
            file.createNewFile();
        }
        return file;
    }

    private static byte[] toBytes(short aShort) {
        return new byte[] {(byte) ((aShort >> 8) & 0xFF), (byte) (aShort & 0xFF)};
    }

    private void checkEnd() {
        Random random = new Random();
        AccessibilityNodeInfo goOnWatch;
        AssistUtil.nodeDispatchGestureInfoClick(1285, 115);
        sleep(1000L);
        goOnWatch = AssistUtil.getFirstNodeInfoByText("继续观看");
        int count = 5;
        while (count > 0 && goOnWatch != null) {
            AssistUtil.nodeDispatchGestureInfoClick(goOnWatch);
            count--;
            sleep((random.nextInt(11) + 10) * 1000L);
            AssistUtil.nodeDispatchGestureInfoClick(1285, 115);
            sleep(1000L);
            goOnWatch = AssistUtil.getFirstNodeInfoByText("继续观看");
        }
    }

    private void countDown(int time) {
        while (time > 0) {
            ToastUtils.showShort(time);
            time--;
            sleep(1000L);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //        LogUtils.d(TAG + " dispatchTouchEvent " + MotionEventUtils.toFlage(ev) + " ");
        boolean result = super.dispatchTouchEvent(ev);
        //        LogUtils.d(
        //                TAG + " dispatchTouchEvent " + MotionEventUtils.toFlage(ev) + " return " +
        // result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d(TAG + " onTouchEvent,action " + MotionEventUtils.toFlage(event) + " ");
        boolean result = super.onTouchEvent(event);
        LogUtils.d(
                TAG
                        + " onTouchEvent,"
                        + MotionEventUtils.toFlage(event)
                        + " return "
                        + result
                        + " ");

        return result;
    }

    public static class NotificationBroadcastReceiver extends BroadcastReceiver {
        public static final String ACTION_CLICK = "com.ytshow.notification.click";
        public static final String ACTION_CANCEL = "com.ytshow.notification.cancel";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.i(intent.getStringExtra("type"));
            String message = intent.getStringExtra("message");
            LogUtils.d("onReceive: message=" + message);
            LogUtils.d("onReceive: message=" + intent.getPackage());
        }
    }

    public static class MyBroadcastReceiver extends BroadcastReceiver {

        private static final String TAG = "BootBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
                Log.d(TAG, "onReceive: 系统开机了" + intent.getAction());
                Toast.makeText(context, "开机啦~~~", Toast.LENGTH_LONG).show();
                /*Intent i = new Intent(context, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(i);*/
            }

            /*Intent intent2 = new Intent(context, MainActivity.class);
            intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent2);*/

        }
    }

    public static class ScannerReceiver extends BroadcastReceiver {

        private static final String TAG = "BootBroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent == null || intent.getAction() == null) {
                return;
            }
            if (intent.getAction().equals(ACTION_MEDIA_SCANNER_STARTED)
                    || intent.getAction().equals(ACTION_MEDIA_SCANNER_FINISHED)
                    || intent.getAction().equals(ACTION_MEDIA_SCANNER_SCAN_FILE)) {
                LogUtils.d("onReceive: action=" + intent.getAction());
                LogUtils.d("onReceive: getDataString=" + intent.getDataString());

                if (intent.getExtras() != null) {
                    Set<String> strings = intent.getExtras().keySet();

                    strings.forEach(
                            new Consumer<String>() {
                                @Override
                                public void accept(String s) {
                                    LogUtils.d(
                                            "onReceive: key="
                                                    + s
                                                    + "\n"
                                                    + "value="
                                                    + intent.getExtras().get(s));
                                }
                            });
                }
            }
        }
    }

    static boolean isVisit[];

    public static void main(String[] args) {
        int map[][] = new int[7][7];
        isVisit = new boolean[7];
        map[0][1] = map[1][0] = 1;
        map[0][2] = map[2][0] = 1;
        map[0][3] = map[3][0] = 1;

        map[1][4] = map[4][1] = 1;
        map[1][5] = map[5][1] = 1;
        map[2][6] = map[6][2] = 1;
        map[3][6] = map[6][3] = 1;

        isVisit[0] = true;
        dfs(0, map); // 从0开始遍历
    }

    private static void dfs(int index, int[][] map) {
        // TODO Auto-generated method stub
        System.out.println("访问" + (index + 1) + "  ");
        for (int i = 0; i < map[index].length; i++) // 查找联通节点
        {
            if (map[index][i] > 0 && !isVisit[i]) {
                isVisit[i] = true;
                dfs(i, map);
            }
        }
        System.out.println((index + 1) + "访问结束 ");

        String s = "2020-04-24time：时间，size：大小36";

        Pair<Integer, Integer> pair = new Pair<>(1, 2);
    }
}
