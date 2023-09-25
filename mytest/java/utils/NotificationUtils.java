package com.example.aidltest.utils;

import android.app.AppOpsManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.aidltest.MyApplication;
import com.example.aidltest.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationUtils {
    public static final String CHANNEL_ID_TASK = "CHANNEL_TASK";
    public static final String CHANNEL_NAME_TASK = "任务通知";
    public static final int CHANNEL_IDENTIFIER_TASK = 100;

    private NotificationManager notificationManager;

    private NotificationUtils() {
        notificationManager =
                (NotificationManager)
                        MyApplication.getContext().getSystemService(NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(
                    CHANNEL_ID_TASK, CHANNEL_NAME_TASK, NotificationManager.IMPORTANCE_HIGH);
        }
    }

    public static NotificationUtils getInstance() {
        return NotificationUtils.SingletonHolder.INSTANCE;
    }

    /** 创建单例 */
    private static class SingletonHolder {
        private static final NotificationUtils INSTANCE = new NotificationUtils();
    }

    // 判断该app是否打开了通知
    /**
     * 可以通过NotificationManagerCompat 中的
     * areNotificationsEnabled()来判断是否开启通知权限。NotificationManagerCompat 在
     * android.support.v4.app包中，是API 22.1.0 中加入的。而 areNotificationsEnabled()则是在 API 24.1.0之后加入的。
     * areNotificationsEnabled 只对 API 19 及以上版本有效，低于API 19 会一直返回true
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public boolean isNotificationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            NotificationManagerCompat notificationManagerCompat =
                    NotificationManagerCompat.from(context);
            return notificationManagerCompat.areNotificationsEnabled();
        }

        String CHECK_OP_NO_THROW = "checkOpNoThrow";
        String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod =
                    appOpsClass.getMethod(
                            CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg)
                    == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    // 打开手机设置页面
    /**
     * 假设没有开启通知权限，点击之后就需要跳转到 APP的通知设置界面，对应的Action是：Settings.ACTION_APP_NOTIFICATION_SETTINGS,
     * 这个Action是 API 26 后增加的 如果在部分手机中无法精确的跳转到 APP对应的通知设置界面，那么我们就考虑直接跳转到
     * APP信息界面，对应的Action是：Settings.ACTION_APPLICATION_DETAILS_SETTINGS
     */
    public void gotoNotificationSet(Context context) {

        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0引导
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // 其他
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void gotoNotificationChannelSet(Context context, String channelId) {
        if (isNotificationEnabled(context)) {
            Intent intent = new Intent();
            intent.setAction("android.settings.CHANNEL_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.CHANNEL_ID", channelId);
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else {
            gotoNotificationSet(context);
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    public void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        channel.setShowBadge(true);
        notificationManager.createNotificationChannel(channel);
    }

    @RequiresApi(Build.VERSION_CODES.HONEYCOMB)
    private Notification createNotificationByHoneycomb(String title, String content) {
        // 在API11之后构建Notification的方式
        Notification.Builder builder =
                new Notification.Builder(MyApplication.getContext()); // 获取一个Notification构造器

        /*Intent nfIntent = new Intent(this, MainActivity.class);*/
        builder /*.setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))*/ // 设置PendingIntent
                .setSmallIcon(R.drawable.ic)
                .setLargeIcon(
                        BitmapFactory.decodeResource(
                                MyApplication.getContext().getResources(),
                                R.drawable.ic)) // 设置下拉列表中的图标(大图标)
                .setContentTitle(title) // 设置下拉列表里的标题
                .setContentText(content) // 设置上下文内容
                .setDefaults(Notification.DEFAULT_ALL)
                .setAutoCancel(true)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        return builder.build();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private Notification createNotificationByO(String channelId, String title, String content) {
        return new NotificationCompat.Builder(MyApplication.getContext(), channelId)
                .setContentTitle(title)
                .setContentText(content)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic)
                .setLargeIcon(
                        BitmapFactory.decodeResource(
                                MyApplication.getContext().getResources(), R.drawable.ic))
                .setAutoCancel(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setDefaults(Notification.DEFAULT_ALL)
                .build();
    }

    public Notification getNotificationTask(String title, String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            return createNotificationByO(CHANNEL_ID_TASK, title, content);
        } else {
            return createNotificationByHoneycomb(title, content);
        }
    }

    public void showNotification(int id, Notification notification) {
        // 参数一：唯一的通知标识；参数二：通知消息。
        notificationManager.notify(id, notification);
    }
}
