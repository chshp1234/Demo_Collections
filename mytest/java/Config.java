package com.example.aidltest;

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

    public static String BASE_URL="https://www.baidu.com/";

    public static Class API_SERVICE;

    /** 用户TOKEN */
    public static final String TOKEN = "token";

    /** json请求 */
    public static final String POST_JSON = "application/json;charset=UTF-8";

    /** form-data */
    public static final String FORM_DATA = "multipart/form-data";

    /** 网络请求超时时间毫秒 */
    public static final int DEFAULT_TIMEOUT = 15000;

    /** 验证码当前时间 */
    public static long VALIDATE_CODE_TIME = 0;

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
