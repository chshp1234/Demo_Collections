package com.example.aidltest.utils;

import android.app.Activity;
import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RomUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.blankj.utilcode.util.Utils;
import com.example.aidltest.R;

import androidx.appcompat.app.AlertDialog;

import static android.app.AppOpsManager.OPSTR_SYSTEM_ALERT_WINDOW;
import static com.example.aidltest.config.RequestCodeConfig.REQUEST_ACCESSIBILITY_CODE;
import static com.example.aidltest.config.RequestCodeConfig.REQUEST_FLOATING_CODE;

public class AccessUtils {

    public static void getAccessibility(Activity activity) {
        AlertDialog accDialog =
                new AlertDialog.Builder(activity)
                        .setMessage("我们需要无障碍权限来协助自动抢红包，请允许授权")
                        .setTitle("无障碍授权")
                        .setCancelable(false)
                        .setPositiveButton(
                                "去设置",
                                (dialog, which) -> {
                                    dialog.cancel();
                                    ToastUtils.showLong("请在无障碍中找到快来淘并开启权限");
                                    Intent intent =
                                            new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                                    activity.startActivityForResult(
                                            intent, REQUEST_ACCESSIBILITY_CODE);
                                })
                        .create();
        accDialog.setCanceledOnTouchOutside(false);
        accDialog.show();
    }

    public static void getOverlay(Activity activity) {
        AlertDialog accDialog =
                new AlertDialog.Builder(activity)
                        .setMessage("我们需要悬浮窗权限来协助自动抢红包，请允许授权")
                        .setTitle("悬浮窗授权")
                        .setCancelable(false)
                        .setPositiveButton(
                                "去设置",
                                (dialog, which) -> {
                                    dialog.cancel();
                                    if (RomUtils.isVivo()) {
                                        AppUtils.launchAppDetailsSettings();
                                    } else {
                                        activity.startActivityForResult(
                                                new Intent(
                                                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                                        Uri.parse(
                                                                "package:"
                                                                        + Utils.getApp()
                                                                                .getPackageName())),
                                                REQUEST_FLOATING_CODE);
                                    }
                                })
                        .create();
        accDialog.setCanceledOnTouchOutside(false);
        accDialog.show();
    }

    /**
     * 检测悬浮窗权限是否开启 Is can draw overlays boolean.
     *
     * @param context the context
     * @return the boolean
     */
    public static boolean isCanDrawOverlays(Context context) {
        if (context == null) return false;
        if (RomUtils.isVivo()) {
            LogUtils.d("isCanDrawOverlays: isVivo");
            return getFloatPermissionStatus(context) == 0;
        } else if (Build.VERSION.SDK_INT >= 23) {
            return Settings.canDrawOverlays(context);
        } else if (Build.VERSION.SDK_INT < 19) {
            return true;
        } else {
            AppOpsManager aom =
                    (AppOpsManager) Utils.getApp().getSystemService(Context.APP_OPS_SERVICE);
            if (aom == null) return false;
            int mode =
                    aom.checkOpNoThrow(
                            OPSTR_SYSTEM_ALERT_WINDOW,
                            android.os.Process.myUid(),
                            Utils.getApp().getPackageName());
            return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
        }
    }

    public static void getScreenShot(Activity activity) {
        ScreenShotUtils.getInstance().requestCapturePermission();
    }

    /**
     * 获取悬浮窗权限状态
     *
     * @param context
     * @return 1或其他是没有打开，0是打开，该状态的定义和{@link AppOpsManager#MODE_ALLOWED}，MODE_IGNORED等值差不多，自行查阅源码
     */
    public static int getFloatPermissionStatus(Context context) {
        if (context == null) {
            return 2;
        }
        String packageName = context.getPackageName();
        Uri uri =
                Uri.parse("content://com.iqoo.secure.provider.secureprovider/allowfloatwindowapp");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[] {packageName};
        Cursor cursor =
                context.getContentResolver().query(uri, null, selection, selectionArgs, null);
        if (cursor != null) {
            cursor.getColumnNames();
            if (cursor.moveToFirst()) {
                int currentmode = cursor.getInt(cursor.getColumnIndex("currentlmode"));
                cursor.close();
                return currentmode;
            } else {
                cursor.close();
                return getFloatPermissionStatus2(context);
            }

        } else {
            return getFloatPermissionStatus2(context);
        }
    }

    /**
     * vivo比较新的系统获取方法
     *
     * @param context
     * @return
     */
    private static int getFloatPermissionStatus2(Context context) {
        String packageName = context.getPackageName();
        Uri uri2 =
                Uri.parse(
                        "content://com.vivo.permissionmanager.provider.permission/float_window_apps");
        String selection = "pkgname = ?";
        String[] selectionArgs = new String[] {packageName};
        Cursor cursor =
                context.getContentResolver().query(uri2, null, selection, selectionArgs, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int currentmode = cursor.getInt(cursor.getColumnIndex("currentmode"));
                cursor.close();
                return currentmode;
            } else {
                cursor.close();
                return 1;
            }
        }
        return 1;
    }
}
