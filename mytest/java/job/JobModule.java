package com.example.aidltest.job;

import android.os.Build;
import android.view.accessibility.AccessibilityNodeInfo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.InitData;
import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.event.FloatEvent;

import static com.example.aidltest.job.BaseJob.ONE_SECOND;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoClick;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoLongClick;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScroll;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScrollBackward;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScrollForward;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScrollLeft;
import static com.taotaosou.accessibility.utils.AssistUtil.nodeDispatchGestureInfoScrollRight;
import static com.taotaosou.accessibility.utils.AssistUtil.nodePerformInfoClick;
import static com.taotaosou.accessibility.utils.AssistUtil.nodePerformInfoLongClick;
import static com.taotaosou.accessibility.utils.AssistUtil.nodePerformInfoScrollBackward;
import static com.taotaosou.accessibility.utils.AssistUtil.nodePerformInfoScrollForward;

public class JobModule {

    private static final long BROWSE_INTERVAL = 500;

    public static void sleep(long time) throws InterruptedException {
        Thread.sleep(time);
        if (!BaseJob.isRun()) {
            LogUtils.e("sleep: ", "中断失败，主动退出");
            throw new InterruptedException();
        }
    }

    public static boolean click(AccessibilityNodeInfo nodeInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return nodeDispatchGestureInfoClick(nodeInfo);
        } else {
            return nodePerformInfoClick(nodeInfo);
        }
    }

    public static boolean click(int x, int y) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return nodeDispatchGestureInfoClick(x, y);
        }
        return false;
    }

    public static boolean longClick(AccessibilityNodeInfo nodeInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            //            return nodePerformInfoLongClick(nodeInfo);
            return nodeDispatchGestureInfoLongClick(nodeInfo, 600);
        } else {
            return nodePerformInfoLongClick(nodeInfo);
        }
    }

    public static void scrollForward(AccessibilityNodeInfo scrollNode) {
        if (scrollNode == null) {
            scrollForward();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScrollForward(scrollNode, BROWSE_INTERVAL);
            //            nodePerformInfoScrollForward(scrollNode);
        } else {
            nodePerformInfoScrollForward(scrollNode);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollForward(float fromX, float fromY, float toX, float toY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(fromX, fromY, toX, toY, BROWSE_INTERVAL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollForward() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(
                    InitData.getInstance().getScreenWidth() >> 1,
                    (InitData.getInstance().getScreenHeight() * 3) >> 2,
                    InitData.getInstance().getScreenWidth() >> 1,
                    InitData.getInstance().getScreenHeight() >> 2,
                    BROWSE_INTERVAL);
        }
    }

    public static void scrollBackward(AccessibilityNodeInfo scrollNode) {
        if (scrollNode == null) {
            scrollBackward();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScrollBackward(scrollNode, BROWSE_INTERVAL);
            //            nodePerformInfoScrollBackward(scrollNode);
        } else {
            nodePerformInfoScrollBackward(scrollNode);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollBackward(float fromX, float fromY, float toX, float toY) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(fromX, fromY, toX, toY, BROWSE_INTERVAL);
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
                    BROWSE_INTERVAL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollLeft() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(
                    (InitData.getInstance().getScreenWidth() * 3) >> 2,
                    InitData.getInstance().getScreenHeight() >> 1,
                    InitData.getInstance().getScreenWidth() >> 2,
                    InitData.getInstance().getScreenHeight() >> 1,
                    BROWSE_INTERVAL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollLeft(AccessibilityNodeInfo nodeInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScrollLeft(nodeInfo, BROWSE_INTERVAL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollRight() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScroll(
                    InitData.getInstance().getScreenWidth() >> 2,
                    InitData.getInstance().getScreenHeight() >> 1,
                    (InitData.getInstance().getScreenWidth() * 3) >> 2,
                    InitData.getInstance().getScreenHeight() >> 1,
                    BROWSE_INTERVAL);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scrollRight(AccessibilityNodeInfo nodeInfo) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            nodeDispatchGestureInfoScrollRight(nodeInfo, BROWSE_INTERVAL);
        }
    }

    interface CustomGetNodeProxy {
        AccessibilityNodeInfo getNode();
    }

    public static AccessibilityNodeInfo scrollForwardToFindNode(
            @NonNull CustomGetNodeProxy getNodeProxy, int count) throws InterruptedException {
        AccessibilityNodeInfo node = getNodeProxy.getNode();
        while ((node == null || !node.isVisibleToUser()) && count > 0) {
            scrollForward();
            sleep(ONE_SECOND);
            node = getNodeProxy.getNode();
            count--;
        }

        return node;
    }
}
