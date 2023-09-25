package com.example.aidltest.utils;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ResolveInfo;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityManager;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AccessibilityCheckUtils {

    private static int getAccessibilityEnabled(Context context) throws Settings.SettingNotFoundException {
        return Settings.Secure.getInt(context.getApplicationContext().getContentResolver(),
                Settings.Secure.ACCESSIBILITY_ENABLED);
    }

    private static String getEnabledAccessibilityServices(Context context) {
        return Settings.Secure.getString(context.getApplicationContext().getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
    }

    /**
     * @param context context
     * @param str     context.getPackageName() + "/" + AccessibilityService.getCanonicalName();
     */
    public static boolean isAccessibilityEnable(Context context, String str) {
        int i;
        try {
            i = getAccessibilityEnabled(context);
        } catch (Settings.SettingNotFoundException e) {
            i = 0;
        }
        TextUtils.SimpleStringSplitter simpleStringSplitter = new TextUtils.SimpleStringSplitter(':');
        if (i != 1) {
            return false;
        }
        String string = getEnabledAccessibilityServices(context);
        if (string == null) {
            return false;
        }
        simpleStringSplitter.setString(string);
        while (simpleStringSplitter.hasNext()) {
            if (simpleStringSplitter.next().equalsIgnoreCase(str)) {
                return true;
            }
        }
        return false;
    }

}
