package com.example.aidltest.utils;

import android.os.Environment;

import java.io.File;

/** Created by Administrator on 2018/8/15. */
public class FilePathConfig {
    public static String EXTERNAL_STORAGE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath();
    public static String APP_NAME = "flowbox";
    public static String APP_PATH = EXTERNAL_STORAGE_PATH + File.separator + APP_NAME;
    public static String APP_LOG = APP_PATH + File.separator + "log";

    public static final String DOWNLOAD = APP_PATH + File.separator + "download";
}
