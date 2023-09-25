package com.example.aidltest;

import com.blankj.utilcode.util.LogUtils;

public class NativeTest {
    static {
        try {
            System.loadLibrary("cms");
        } catch (UnsatisfiedLinkError e) {
            LogUtils.e("static initializer: ", e);
        }
    }

    public static native byte[] leviathan(int i, byte[] bArr);
}
