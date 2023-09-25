package com.csp.mylocation

import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build.VERSION_CODES.Q
import androidx.core.content.ContextCompat.getSystemService
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.blankj.utilcode.util.AppUtils.getAppName
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.Utils

/**
 * created by dongdaqing 2022/2/24 3:44 下午
 */
object MyLocationUtils {
    //声明AMapLocationClient类对象
    @SuppressLint("StaticFieldLeak")
    private var mLocationClient: AMapLocationClient? = null

    fun init(context: Application) {

    }

    fun startLocation(mAMapLocationListener: AMapLocationListener) {
        checkPermission {
            if (mLocationClient == null) {
                AMapLocationClient.updatePrivacyShow(Utils.getApp(), true, true)
                AMapLocationClient.updatePrivacyAgree(Utils.getApp(), true)
//初始化定位
                mLocationClient = AMapLocationClient(Utils.getApp())
                //设置定位回调监听
                mLocationClient!!.setLocationListener(mAMapLocationListener)

                //设置定位间隔,单位毫秒,默认为2000ms，最低1000ms。
                val option = AMapLocationClientOption()
                option.interval = 5000
//设置是否返回地址信息（默认返回地址信息）
                option.isNeedAddress = true;

                mLocationClient!!.setLocationOption(option)

                mLocationClient!!.stopLocation()
            }
//启动定位
            mLocationClient!!.startLocation()

            //启动后台定位，第一个参数为通知栏ID，建议整个APP使用一个
            mLocationClient!!.enableBackgroundLocation(2001, buildNotification())
        }
    }

    fun stopLocation() {
        mLocationClient?.stopLocation();//停止定位后，本地定位服务并不会被销毁
//关闭后台定位，参数为true时会移除通知栏，为false时不会移除通知栏，但是可以手动移除
        mLocationClient?.disableBackgroundLocation(true);
    }

    private fun checkPermission(start: () -> Unit) {
        if (android.os.Build.VERSION.SDK_INT >= Q) {
            PermissionUtils.permission(ACCESS_BACKGROUND_LOCATION).callback { _, _, _, _ ->
                start()
            }
        } else {
            PermissionUtils.permission(ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION)
                .callback { _, _, _, _ ->
                    start()
                }
        }.request()
    }

    private const val NOTIFICATION_CHANNEL_NAME = "BackgroundLocation"
    private var notificationManager: NotificationManager? = null
    private var isCreateChannel = false

    @SuppressLint("NewApi")
    fun buildNotification(): Notification {

        val builder: Notification.Builder?
        val notification: Notification?
        if (android.os.Build.VERSION.SDK_INT >= 26) {
            //Android O上对Notification进行了修改，如果设置的targetSDKVersion>=26建议使用此种方式创建通知栏
            if (null == notificationManager) {
                notificationManager =
                    getSystemService(Utils.getApp(), NotificationManager::class.java)
            }
            val channelId = Utils.getApp().packageName
            if (!isCreateChannel) {
                val notificationChannel = NotificationChannel(
                    channelId,
                    NOTIFICATION_CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationChannel.enableLights(true);//是否在桌面icon右上角展示小圆点
                notificationChannel.lightColor = Color.BLUE; //小圆点颜色
                notificationChannel.setShowBadge(true); //是否在久按桌面图标时显示此渠道的通知
                notificationManager!!.createNotificationChannel(notificationChannel);
                isCreateChannel = true;
            }
            builder = Notification.Builder(Utils.getApp(), channelId);
        } else {
            builder = Notification.Builder(Utils.getApp());
        }
        builder.setSmallIcon(R.drawable.ic_launcher)
            .setContentTitle(getAppName())
            .setContentText("正在后台运行")
            .setWhen(System.currentTimeMillis());

        if (android.os.Build.VERSION.SDK_INT >= 16) {
            notification = builder.build();
        } else {
            return builder.getNotification();
        }
        return notification;
    }
}
