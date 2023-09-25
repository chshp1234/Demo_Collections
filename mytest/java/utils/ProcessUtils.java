package com.example.aidltest.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;


import com.example.aidltest.MyApplication;

import java.util.ArrayList;
import java.util.List;

public class ProcessUtils {

    private static final String TAG = "ProcessUtils";
    private ActivityManager mActivityManager;
    private ActivityManager.MemoryInfo memInfo;
    private Debug.MemoryInfo debugInfo;

    private ProcessUtils() {
        mActivityManager =
                (ActivityManager)
                        MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        memInfo = new ActivityManager.MemoryInfo();
        debugInfo = new Debug.MemoryInfo();
    }

    public static ProcessUtils getInstance() {
        return ProcessUtils.SingletonHolder.INSTANCE;
    }

    /** 创建单例 */
    private static class SingletonHolder {
        private static final ProcessUtils INSTANCE = new ProcessUtils();
    }

    /** 获得系统进程信息 */
    public List getRunningAppProcessInfo() {
        // ProcessInfo Model类   用来保存所有进程信息
        List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();

        // 通过调用ActivityManager的getRunningAppProcesses()方法获得系统里所有正在运行的进程
        List<ActivityManager.RunningAppProcessInfo> appProcessList =
                mActivityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo appProcessInfo : appProcessList) {
            // 进程ID号
            int pid = appProcessInfo.pid;
            // 用户ID 类似于Linux的权限不同，ID也就不同 比如 root等
            int uid = appProcessInfo.uid;
            // 进程名，默认是包名或者由属性android：process=""指定
            String processName = appProcessInfo.processName;
            // 获得该进程占用的内存
            int[] myMempid = new int[] {pid};
            // 此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = mActivityManager.getProcessMemoryInfo(myMempid);
            // 获取进程占内存用信息 kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;

            Log.i(
                    TAG,
                    "processName: "
                            + processName
                            + "  pid: "
                            + pid
                            + " uid:"
                            + uid
                            + " memorySize is -->"
                            + memSize
                            + "kb");
            // 构造一个ProcessInfo对象
            ProcessInfo processInfo = new ProcessInfo();
            processInfo.setPid(pid);
            processInfo.setUid(uid);
            processInfo.setMemSize(memSize);
            processInfo.setPocessName(processName);
            processInfoList.add(processInfo);

            // 获得每个进程里运行的应用程序(包),即每个应用程序的包名
            String[] packageList = appProcessInfo.pkgList;
            Log.i(TAG, "process id is " + pid + "has " + packageList.length);
            for (String pkg : packageList) {
                Log.i(TAG, "packageName " + pkg + " in process id is -->" + pid);
            }
        }
        return processInfoList;
    }

    public static class ProcessInfo {

        // 进程id  Android规定android.system.uid=1000
        private int pid;
        // 进程所在的用户id ，即该进程是有谁启动的 root/普通用户等
        private int uid;
        // 进程占用的内存大小,单位为kb
        private int memSize;
        // 进程名
        private String processName;

        public ProcessInfo() {}

        public int getPid() {
            return pid;
        }

        public void setPid(int pid) {
            this.pid = pid;
        }

        public int getUid() {
            return uid;
        }

        public void setUid(int uid) {
            this.uid = uid;
        }

        public int getMemSize() {
            return memSize;
        }

        public void setMemSize(int memSize) {
            this.memSize = memSize;
        }

        public String getProcessName() {
            return processName;
        }

        public void setPocessName(String processName) {
            this.processName = processName;
        }
    }
}
