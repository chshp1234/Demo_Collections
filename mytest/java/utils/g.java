package com.example.aidltest.utils;

import com.blankj.utilcode.util.LogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class g {
    public static final boolean pd = D(System.getProperty("java.vm.version"));

    private static boolean D(String str) {
        boolean z = false;
        if (str != null) {
            Matcher matcher = Pattern.compile("(\\d+)\\.(\\d+)(\\.\\d+)?").matcher(str);
            if (matcher.matches()) {
                try {
                    int parseInt = Integer.parseInt(matcher.group(1));
                    int parseInt2 = Integer.parseInt(matcher.group(2));
                    if (parseInt > 2 || (parseInt == 2 && parseInt2 > 0)) {
                        z = true;
                    }
                } catch (NumberFormatException e) {
                }
            }
        }
        LogUtils.i(
                "MicroMsg.MultiDex",
                "VM with version "
                        + str
                        + (z ? " has multidex support" : " does not have multidex support"));
        return z;
    }
}
