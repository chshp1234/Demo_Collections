package com.example.aidltest.job;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.accessibility.AccessibilityWindowInfo;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ObjectUtils;
import com.blankj.utilcode.util.StringUtils;
import com.taotaosou.accessibility.utils.AssistUtil;

import java.util.List;

public class AssisProxy {
    public static AccessibilityService assistService;

    public static void init(AccessibilityService service) {
        assistService = service;
    }

    public static List<AccessibilityWindowInfo> getRootWindows() {
        if (assistService == null) return null;
        List<AccessibilityWindowInfo> windows = assistService.getWindows();
        return windows;
    }

    public static AccessibilityNodeInfo getFirstNodeInfoByViewId(String viewId) {
        List<AccessibilityWindowInfo> windowInfos = getRootWindows();
        if (ObjectUtils.isEmpty(windowInfos) || StringUtils.isTrimEmpty(viewId)) {
            return null;
        }

        List<AccessibilityNodeInfo> nodeInfos;
        for (AccessibilityWindowInfo accessibilityWindowInfo : windowInfos) {
            nodeInfos =
                    accessibilityWindowInfo.getRoot().findAccessibilityNodeInfosByViewId(viewId);
            if (!ObjectUtils.isEmpty(nodeInfos)) {
                return nodeInfos.get(0);
            }
        }

        return null;
    }

    public static AccessibilityNodeInfo getFirstExactNodeInfoByText(String viewText) {
        List<AccessibilityWindowInfo> windowInfos = getRootWindows();
        if (ObjectUtils.isEmpty(windowInfos) || StringUtils.isTrimEmpty(viewText)) {
            return null;
        }

        List<AccessibilityNodeInfo> nodeInfos;
        for (AccessibilityWindowInfo accessibilityWindowInfo : windowInfos) {
            nodeInfos =
                    accessibilityWindowInfo.getRoot().findAccessibilityNodeInfosByText(viewText);
            if (!ObjectUtils.isEmpty(nodeInfos)) {
                return nodeInfos.get(0);
            }
        }

        return null;
    }

    public static AccessibilityNodeInfo getFirstExactNodeInfoByViewText(String viewText) {
        List<AccessibilityWindowInfo> windowInfos = getRootWindows();
        if (ObjectUtils.isEmpty(windowInfos) || StringUtils.isTrimEmpty(viewText)) {
            return null;
        }

        List<AccessibilityNodeInfo> nodeInfos;
        for (AccessibilityWindowInfo accessibilityWindowInfo : windowInfos) {
            nodeInfos =
                    accessibilityWindowInfo.getRoot().findAccessibilityNodeInfosByText(viewText);
            if (!ObjectUtils.isEmpty(nodeInfos)) {
                for (AccessibilityNodeInfo textNode : nodeInfos) {
                    LogUtils.d(textNode.getText());
                    if (AssistUtil.getNodeInfoTextByNode(textNode).equals(viewText)) {
                        LogUtils.d(textNode.getText());
                        return textNode;
                    }
                }
            }
        }

        return null;
    }
}
