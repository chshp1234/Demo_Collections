package com.example.aidltest.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.RomUtils;
import com.example.aidltest.MyApplication;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

public class CustomAppUtlis {
    /**
     * 跳转到目标Activity
     *
     * @param pkg The name of the package that the component exists in. Can not be null
     * @return the boolean
     */
    public static void gotoTargetActivity(String pkg) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            intent.setPackage(pkg);
        }
        try {
            List<ResolveInfo> apps =
                    MyApplication.getContext().getPackageManager().queryIntentActivities(intent, 0);
            LogUtils.d("gotoTargetActivity: app count:" + apps.size());
            for (ResolveInfo rInfo : apps) {
                if (rInfo.activityInfo.packageName.equals(pkg)) {
                    gotoTargetActivity(
                            pkg,
                            rInfo.activityInfo.name,
                            Intent.ACTION_MAIN,
                            Intent.CATEGORY_LAUNCHER);
                }
            }
        } catch (Exception e) {
            LogUtils.e("gotoTargetActivity: ", e);
        }
    }

    /**
     * 跳转到目标Activity
     *
     * @param pkg The name of the package that the component exists in. Can not be null
     * @param cls the cls
     * @param action An action name, such as ACTION_VIEW. Application-specific actions should be
     *     prefixed with the vendor's package name
     * @param category The desired category. This can be either one of the predefined Intent
     *     categories, or a custom category in your own namespace
     * @return the boolean
     */
    public static void gotoTargetActivity(
            String pkg, String cls, String action, String... category) {

        Intent intent = new Intent();
        ComponentName cmp = new ComponentName(pkg, cls);
        if (action != null) {
            intent.setAction(action);
        }

        if (category != null && category.length > 0) {
            for (String cat : category) intent.addCategory(cat);
        }
        intent.setComponent(cmp);
        gotoTargetActivity(intent);
    }

    /**
     * Goto target activity boolean.
     *
     * @param intent the intent
     * @return the boolean
     */
    public static void gotoTargetActivity(Intent intent) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MyApplication.getContext().startActivity(intent);
    }

    /**  判断服务是否启动,context上下文对象 ，className服务的name */
    public static boolean isServiceRunning(Context mContext, String className) {
        boolean isRunning = false;
        ActivityManager activityManager =
                (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList =
                activityManager.getRunningServices(Integer.MAX_VALUE);

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

    /**
     * Go to tao specific activity.
     *
     * @param className the class name
     * @param url the url
     */
    public static boolean goToTaoSpecificActivity(String className, String url) {
        try {
            Intent intent = new Intent();
            intent.setAction("Android.intent.action.VIEW");
            Uri uri = Uri.parse(url); // 商品地址
            intent.setData(uri);
            intent.setClassName(PackageInfoConfig.TAOBAO_PACKAGE_NAME, className);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MyApplication.getContext().startActivity(intent);
            return true;
        } catch (Exception e) {
            LogUtils.e("Exception: " + e);
            return false;
        }
    }

    /**
     * Gets launcher intent.
     *
     * @param pkgName the pkg name
     * @return the launcher intent
     */
    public static Intent getLauncherIntent(String pkgName) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        for (ResolveInfo rInfo :
                MyApplication.getContext().getPackageManager().queryIntentActivities(intent, 0)) {
            if (rInfo.activityInfo.packageName.equals(pkgName)) {
                ComponentName cmp = new ComponentName(pkgName, rInfo.activityInfo.name);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setComponent(cmp);
                return intent;
            }
        }
        return null;
    }

    /** 跳转到权限设置面 */
    public static void jumpPermissionPage(Context context, String packageName) {
        if (RomUtils.isXiaomi()) {
            goXiaoMiMainager(context, packageName);
        } else if (RomUtils.isVivo()) {
            goVivoMainager(context, packageName);
        }
    }

    private static void goXiaoMiMainager(Context context, String packageName) {
        try {
            String rom = getMiuiVersion();
            Intent intent = new Intent();
            if ("V6".equals(rom) || "V7".equals(rom)) {
                intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
                intent.putExtra("extra_pkgname", packageName);
            } else if ("V8".equals(rom) || "V9".equals(rom) || "V10".equals(rom)) {
                intent.setAction("miui.intent.action.APP_PERM_EDITOR");
                intent.setClassName(
                        "com.miui.securitycenter",
                        "com.miui.permcenter.permissions.PermissionsEditorActivity");
                intent.putExtra("extra_pkgname", packageName);
            } else {
                AppUtils.launchAppDetailsSettings(packageName);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            AppUtils.launchAppDetailsSettings(packageName);
            LogUtils.e("goXiaoMiMainager: ", e);
        }
    }

    private static void goVivoMainager(Context context, String packageName) {

        AppUtils.launchAppDetailsSettings(packageName);
        /*   Intent openQQ = getPackageManager().getLaunchIntentForPackage("com.vivo.securedaemonservice");
        startActivity(openQQ);*/
    }

    private static String getMiuiVersion() {
        String propName = "ro.miui.ui.version.name";
        String line;
        BufferedReader input = null;
        try {
            Process p = Runtime.getRuntime().exec("getprop " + propName);
            input = new BufferedReader(new InputStreamReader(p.getInputStream()), 1024);
            line = input.readLine();
            input.close();
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return line;
    }
}
