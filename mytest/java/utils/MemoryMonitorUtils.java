package com.example.aidltest.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Debug;
import android.util.Log;

import com.blankj.utilcode.util.ShellUtils;
import com.example.aidltest.MyApplication;

public class MemoryMonitorUtils {
    private ActivityManager activityManager;
    private ActivityManager.MemoryInfo memInfo;
    private Debug.MemoryInfo debugInfo;

    private MemoryMonitorUtils() {
        activityManager =
                (ActivityManager)
                        MyApplication.getContext().getSystemService(Context.ACTIVITY_SERVICE);
        memInfo = new ActivityManager.MemoryInfo();
        debugInfo = new Debug.MemoryInfo();
    }

    public static MemoryMonitorUtils getInstance() {
        return MemoryMonitorUtils.SingletonHolder.INSTANCE;
    }

    /** 创建单例 */
    private static class SingletonHolder {
        private static final MemoryMonitorUtils INSTANCE = new MemoryMonitorUtils();
    }

    /** Check memory. */
    public synchronized String checkMemory() {
        activityManager.getMemoryInfo(memInfo);
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder
                // 表示系统剩余内存
                .append("\n总共的内存量(totalMem):")
                .append(memInfo.totalMem / 1024 / 1024)
                .append(" mb")
                .append("\n")
                .append("可用的空闲内存量(availMem):")
                .append(memInfo.availMem / 1024 / 1024)
                .append(" mb")
                .append("\n")
                // 它表示当系统剩余内存低于好多时就看成低内存运行
                .append("处于低内存状态的阈值(threshold):")
                .append(memInfo.threshold / 1024 / 1024)
                .append(" mb")
                .append("\n")
                // 它是boolean值，表示系统是否处于低内存运行
                .append("处于低内存状态(lowMemory):")
                .append(memInfo.lowMemory)
                .append("\n");
        Debug.getMemoryInfo(debugInfo);
        int privateDirty = debugInfo.getTotalPrivateDirty(); // 获取USS数据。
        int totalPss = debugInfo.getTotalPss(); // 获取PSS数据。
        int sharedDirty =
                debugInfo.getTotalSharedDirty() + debugInfo.getTotalPrivateDirty(); // 获取RSS数据。
        /*VSS- Virtual Set Size       虚拟耗用内存（包含共享库占用的内存）
        RSS- Resident Set Size      实际使用物理内存（包含共享库占用的内存）
        PSS- Proportional Set Size  实际使用的物理内存（比例分配共享库占用的内存）
            - PSS 与 RSS 的区别是按比例分配，也就是如果三个进程都使用了同一个共享库(占30页内存)，
            那么 PSS 认为每个进程占10页内存
        USS- Unique Set Size        进程独占的物理内存（不包含共享库占用的内存）
            - USS 是非常有用的数据，因为它反映了运行一个特定进程真实的成本（增量成本）。
            当一个进程被销毁后，USS 是真实返回给系统的内存。
            当进程中存在一个可疑的内存泄露时，USS 是最佳观察数据。

        一般来说内存占用大小有如下规律：VSS >= RSS >= PSS >= USS！*/
        stringBuilder
                .append("实际使用物理内存（包含共享库占用的内存）(RSS):")
                .append(sharedDirty / 1024)
                .append(" mb")
                .append("\n")
                .append("实际使用的物理内存（比例分配共享库占用的内存）(PSS):")
                .append(totalPss / 1024)
                .append(" mb")
                .append("\n")
                .append("进程独自占用的物理内存（不包含共享库占用的内存）(USS):")
                .append(privateDirty / 1024)
                .append(" mb")
                .append("\n");
        Log.d("MemoryInfo", stringBuilder.toString());
        return stringBuilder.toString();
    }

    public synchronized String checkAllMemory() {
        ShellUtils.CommandResult dumpsys_meminfo = ShellUtils.execCmd("dumpsys meminfo", false);
        Log.d("AllMemoryInfo", dumpsys_meminfo.successMsg);
        return dumpsys_meminfo.successMsg;
    }

    /*public synchronized String checkMemory(int pid){
        activityManager.getProcessMemoryInfo()
        Debug.getMemoryInfo(debugInfo);

    }*/
}
