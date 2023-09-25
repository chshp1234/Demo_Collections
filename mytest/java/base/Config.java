package com.example.aidltest.base;

import android.os.Environment;

import com.blankj.utilcode.util.Utils;

import java.io.File;

/**
 * 一些配置信息
 *
 * @author csp
 * @date 2017/9/22
 */
public class Config {

    /** 缓存路径 */
    public static final String CACHE_PATH;

    static {
        File cacheDir = Utils.getApp().getExternalCacheDir();
        if (cacheDir != null) {
            CACHE_PATH = cacheDir.getAbsolutePath();
        } else {
            CACHE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
    }
}
