package com.example.aidltest;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.config.FilePathConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class MyApplication extends Application {
    private static final String TAG = "MyApplication";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        String processNameWithPid = getProcessNameWithPid(Process.myPid());
        LogUtils.d("onCreate: process=" + processNameWithPid);

        if (getPackageName().equals(processNameWithPid)) {
            Log.d(TAG, "onReceive: APP 启动");
            LogUtils.getConfig()
                    .setLogSwitch(true)
                    .setLog2FileSwitch(false)
                    .setConsoleSwitch(true)
                    .setDir(FilePathConfig.APP_PATH + File.separator + "log")
                    .setStackDeep(4);
        }
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    private String getProcessNameWithPid(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    // 取得进程名
    public static String getProcessName() {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == android.os.Process.myPid()) {
                return procInfo.processName;
            }
        }
        return null;
    }

    public static Context getContext() {
        return context;
    }
}
