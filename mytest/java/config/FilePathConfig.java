package com.example.aidltest.config;

import android.os.Environment;

import com.example.aidltest.MyApplication;

import java.io.File;

/** Created by Administrator on 2018/8/15. */
public class FilePathConfig {
    public static final String EXTERNAL_STORAGE_PATH =
            Environment.getExternalStorageDirectory().getAbsolutePath();
    public static final String DATA_PATH = Environment.getDataDirectory().getAbsolutePath();
    public static final String CONTEXT_DATA_PATH =
            MyApplication.getContext().getDataDir().getAbsolutePath();
    public static final String CONTEXT_CACHE_PATH =
            MyApplication.getContext().getCacheDir().getAbsolutePath();
    public static final String CONTEXT_FILES_PATH =
            MyApplication.getContext().getFilesDir().getAbsolutePath();
    public static final String CONTEXT_OBB_PATH =
            MyApplication.getContext().getObbDir().getAbsolutePath();
    public static final String CONTEXT_EXTERNAL_PICTURES_PATH =
            MyApplication.getContext()
                    .getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                    .getAbsolutePath();
    public static final String APP_NAME = "MyTest";
    public static final String APP_PATH = EXTERNAL_STORAGE_PATH + File.separator + APP_NAME;

    public static final String DOWNLOAD = APP_PATH + File.separator + "download";
    public static final String APP_PIC = APP_PATH + File.separator + "picture";
    public static final String APP_AUDIO = APP_PATH + File.separator + "audio";
}
