package com.example.aidltest.utils;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.TimeUtils;
import com.blankj.utilcode.util.Utils;
import com.blankj.utilcode.util.ZipUtils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LogFileUtils {
    public static final String LOG_NET = FilePathConfig.APP_LOG + File.separator + "net";
    public static final String LOG_TASK = FilePathConfig.APP_LOG + File.separator + "task";
    public static final String LOG_SCRIPT = FilePathConfig.APP_LOG + File.separator + "script";
    public static final String LOG_NODE_INFO = FilePathConfig.APP_LOG + File.separator + "node";
    public static final String LOG_ZIP_TEMP = FilePathConfig.APP_LOG + File.separator + "temp";
    public static final String LOG_CRASH = FilePathConfig.APP_LOG + File.separator + "crash";
    public static final String LOG_SYSTEM = FilePathConfig.APP_LOG + File.separator + "system";

    public static final int V = Log.VERBOSE;
    public static final int D = Log.DEBUG;
    public static final int I = Log.INFO;
    public static final int W = Log.WARN;
    public static final int E = Log.ERROR;
    public static final int A = Log.ASSERT;

    private static final char[] T = new char[] {'V', 'D', 'I', 'W', 'E', 'A'};
    private static final String FILE_SEP = System.getProperty("file.separator");
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final ExecutorService EXECUTOR = Executors.newSingleThreadExecutor();
    /*todo 使用？*/
    private static final ThreadLocal<SimpleDateFormat> SDF_THREAD_LOCAL = new ThreadLocal<>();
    private static long mSaveDays = -1;
    private static String PATTERN_FILE_NET = "yyyy-MM-dd";
    private static String PATTERN_FILE_SCRIPT = "yyyy-MM-dd";
    private static String PATTERN_FILE_TASK = "yyyy-MM-dd";
    private static String CUSTOM_PATH;

    public static String zipLogs(String dir) {
        createOrExistsDir(new File(LOG_ZIP_TEMP));
        File file = new File(dir);
        if (!FileUtils.isDir(file)) return null;
        List<File> fileList = FileUtils.listFilesInDir(file);
        String filePrefix = getPrefix();
        if (fileList.size() > 0) {
            List<File> renameFiles = new ArrayList<>();
            for (File filterFile : fileList) {
                if (filterFile.getName().contains(filePrefix)) {
                    //                    zipFiles.add(filterFile);
                    String rename =
                            filePrefix
                                    + "-"
                                    + TimeUtils.millis2String(
                                            System.currentTimeMillis(),
                                            new SimpleDateFormat(
                                                    "yyyyMMddHHmmss", Locale.getDefault()))
                                    + ".txt";
                    if (FileUtils.rename(filterFile, rename)) {
                        renameFiles.add(new File(dir, rename));
                    } else {
                        renameFiles.add(filterFile);
                    }
                }
            }
            List<File> zipFiles = new ArrayList<>(renameFiles);
            if (zipFiles.size() > 0) {

                File zipToPath =
                        new File(
                                LOG_ZIP_TEMP
                                        + File.separator
                                        + "klt_"
                                        + filePrefix
                                        + "_"
                                        + TimeUtils.millis2String(
                                                System.currentTimeMillis(),
                                                new SimpleDateFormat(
                                                        "yyyyMMddHHmmss", Locale.getDefault()))
                                        + ".zip");
                try {
                    if (ZipUtils.zipFiles(zipFiles, zipToPath)) {
                        for (File deleteFile : renameFiles) {
                            /*LogUtils.d(
                                    "zipLogs: delete file:"
                                            + deleteFile.getAbsolutePath()
                                            + "-"
                                            + FileUtils.deleteFile(deleteFile));*/
                        }
                        return zipToPath.getAbsolutePath();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void fileTask(String tag, String content) {
        processLog(LOG_TASK + File.separator, getPrefix(), D, tag, content, PATTERN_FILE_TASK);
    }

    public static void fileTask(int type, String tag, String content) {
        processLog(LOG_TASK + File.separator, getPrefix(), type, tag, content, PATTERN_FILE_TASK);
    }

    public static void fileNet(String tag, String content) {
        processLog(LOG_NET + File.separator, getPrefix(), E, tag, content, PATTERN_FILE_NET);
    }

    public static void fileSystem(String tag, String content) {
        processLog(LOG_SYSTEM + File.separator, getPrefix(), E, tag, content, PATTERN_FILE_NET);
    }

    public static void fileScript(String tag, String content) {
        processLog(LOG_SCRIPT + File.separator, getPrefix(), D, tag, content, PATTERN_FILE_SCRIPT);
    }

    public static void fileNodeInfo(String tag, String content) {
        processLog(
                LOG_NODE_INFO + File.separator, getPrefix(), D, tag, content, PATTERN_FILE_SCRIPT);
    }

    public static String getPrefix() {
        return "debug";
    }

    private static void processLog(
            final String dirPath,
            final String filePrefix,
            final int type,
            final String tag,
            final String contents,
            final String pattern) {
        int type_low = type & 0x0f;
        print2File(dirPath, filePrefix, type_low, tag, contents, pattern);
    }

    private static void print2File(
            final String dirPath,
            final String filePrefix,
            final int type,
            final String tag,
            final String msg,
            final String pattern) {
        Date now = new Date(System.currentTimeMillis());
        String date = new SimpleDateFormat(pattern, Locale.getDefault()).format(now);
        String time = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(now);
        final String fullPath = dirPath + filePrefix + "-" + date + ".txt";
        if (!createOrExistsFile(fullPath, pattern)) {
            Log.e("LogUtils", "create " + fullPath + " failed!");
            return;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(time)
                .append(" ")
                .append(T[type - V])
                .append("/")
                .append(tag)
                .append("——")
                .append(msg)
                .append(LINE_SEP);
        final String content = sb.toString();
        input2File(content, fullPath);
    }

    private static boolean createOrExistsFile(final String filePath, final String pattern) {
        File file = new File(filePath);
        if (file.exists()) return file.isFile();
        if (!createOrExistsDir(file.getParentFile())) return false;
        try {
            deleteDueLogs(filePath, pattern);
            boolean isCreate = file.createNewFile();
            if (isCreate) {
                printDeviceInfo(filePath);
            }
            return isCreate;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private static void deleteDueLogs(String filePath, String pattern) {
        File file = new File(filePath);
        File parentFile = file.getParentFile();
        /*File[] files =
        parentFile.listFiles(
                (dir, name) ->
                        name.matches(
                                "^" + filePrefix + "-[0-9]{4}-[0-9]{2}-[0-9]{2}.txt$"));*/
        List<File> files = FileUtils.listFilesInDir(parentFile);
        if (files.size() <= 0) return;
        final int length = filePath.length();
        final SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.getDefault());
        try {
            String curDay = filePath.substring(length - (pattern.length() + 4), length - 4);
            long curDayMill = sdf.parse(curDay).getTime();
            long dueMillis = curDayMill - mSaveDays * 86400000L;
            for (final File aFile : files) {
                try {
                    String name = aFile.getName();
                    int l = name.length();
                    String logDay = name.substring(l - (pattern.length() + 4), l - 4);
                    if (sdf.parse(logDay).getTime() <= dueMillis) {
                        EXECUTOR.execute(
                                () -> {
                                    boolean delete = aFile.delete();
                                    if (!delete) {
                                        Log.e("LogUtils", "delete " + aFile + " failed!");
                                    }
                                });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void printDeviceInfo(final String filePath) {
        String versionName = "";
        int versionCode = 0;
        try {
            PackageInfo pi =
                    Utils.getApp()
                            .getPackageManager()
                            .getPackageInfo(Utils.getApp().getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
                versionCode = pi.versionCode;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        String time = filePath.substring(filePath.length() - 14, filePath.length() - 4);
        final String head =
                "************* Log Head ****************"
                        + "\nDate of Log        : "
                        + time
                        + "\nDevice Manufacturer: "
                        + Build.MANUFACTURER
                        + "\nDevice Model       : "
                        + Build.MODEL
                        + "\nAndroid Version    : "
                        + Build.VERSION.RELEASE
                        + "\nAndroid SDK        : "
                        + Build.VERSION.SDK_INT
                        + "\nApp VersionName    : "
                        + versionName
                        + "\nApp VersionCode    : "
                        + versionCode
                        + "\nTao VersionName    : "
                        + AppUtils.getAppVersionName(PackageInfoConfig.TAOBAO_PACKAGE_NAME)
                        + "\nTao VersionCode    : "
                        + AppUtils.getAppVersionCode(PackageInfoConfig.TAOBAO_PACKAGE_NAME)
                        + "\n************* Log Head ****************\n\n";
        input2File(head, filePath);
    }

    private static void input2File(final String input, final String filePath) {
        EXECUTOR.execute(
                () -> {
                    BufferedWriter bw = null;
                    try {
                        bw = new BufferedWriter(new FileWriter(filePath, true));
                        bw.write(input);
                    } catch (IOException e) {
                        e.printStackTrace();
                        Log.e("LogUtils", "log to " + filePath + " failed!");
                    } finally {
                        try {
                            if (bw != null) {
                                bw.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    private static boolean createOrExistsDir(final File file) {
        return file != null && (file.exists() ? file.isDirectory() : file.mkdirs());
    }

    private static boolean isSpace(final String s) {
        if (s == null) return true;
        for (int i = 0, len = s.length(); i < len; ++i) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static long getmSaveDays() {
        return mSaveDays;
    }

    public static void setmSaveDays(long mSaveDays) {
        LogFileUtils.mSaveDays = mSaveDays;
    }

    public static String getCustomPath() {
        return CUSTOM_PATH;
    }

    public static void setCustomPath(String customPath) {
        CUSTOM_PATH = customPath;
    }
}
