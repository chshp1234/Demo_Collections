//package com.example.aidltest.utils;
//
//import android.app.Activity;
//import android.app.DownloadManager;
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentFilter;
//import android.database.ContentObserver;
//import android.database.Cursor;
//import android.net.Uri;
//import android.os.Build;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
//import com.blankj.utilcode.util.FileUtils;
//import com.cheerslife.plat.Plat;
//import com.cheerslife.utils.VLog;
//
//import java.io.File;
//import java.util.Objects;
//
//import static android.os.Environment.DIRECTORY_DOWNLOADS;
//
//public class DownloadUtils {
//
//    private DownloadManager mDownloadManager;
//    private Activity mActivity;
//    private long mDownloadId;
//    private String mFilePath;
//    private String mAPKName;
//
//    public DownloadUtils(Activity activity) {
//        this.mActivity = activity;
//    }
//
//    // 下载apk
//    public void downloadAPK(String url, String name) {
//        if (FileUtils.deleteFile(
//                Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
//                        .getAbsolutePath())) {
//            VLog.e("Tag", "downloadAPK: 删除成功");
//        } else {
//            VLog.e("Tag", "downloadAPK: 删除失败");
//        }
//        mAPKName = name;
//        VLog.e("Tag", "downloadAPK: 开始下载");
//        // 创建下载任务
//        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
//        // 移动网络情况下是否允许漫游
//        request.setAllowedOverRoaming(true);
//
//        // 在通知栏中显示，默认就是显示的
//        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
//        request.setMimeType("application/vnd.android.package-archive");
//        request.setTitle("新版本Apk");
//        request.setDescription("正在下载安装包...");
//        request.setVisibleInDownloadsUi(true);
//
//        mFilePath =
//                Environment.getExternalStoragePublicDirectory(DIRECTORY_DOWNLOADS)
//                        .getAbsolutePath();
//        // 设置下载的路径
//        request.setDestinationInExternalPublicDir(DIRECTORY_DOWNLOADS, mAPKName);
//
//        // 获取DownloadManager
//        mDownloadManager = (DownloadManager) Plat.app.getSystemService(Context.DOWNLOAD_SERVICE);
//        // 将下载请求加入下载队列，加入下载队列后会给该任务返回一个long型的id，通过该id可以取消任务，重启任务、获取下载的文件等等
//        mDownloadId = mDownloadManager.enqueue(request);
//
//        // 注册广播接收者，监听下载状态
//        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
//        filter.addAction(DownloadManager.ACTION_NOTIFICATION_CLICKED);
//        Plat.app.registerReceiver(receiver, filter);
//
//        /*Uri uri = Uri.parse("content://downloads/all_downloads/" + mDownloadId);;
//        VLog.e("Tag", "uri: " + uri.toString());
//        mActivity.getContentResolver().registerContentObserver(uri, true, new DownloadContentObserver());*/
//    }
//
//    // 广播监听下载的各个状态
//    private BroadcastReceiver receiver =
//            new BroadcastReceiver() {
//                @Override
//                public void onReceive(Context context, Intent intent) {
//                    if (Objects.equals(
//                            intent.getAction(), DownloadManager.ACTION_NOTIFICATION_CLICKED)) {
//                        VLog.e("Tag", "onReceive: Clicked");
//                    } else {
//                        checkStatus();
//                    }
//                }
//            };
//
//    private Handler handler =
//            new Handler(Looper.getMainLooper()) {
//                @Override
//                public void handleMessage(Message msg) {
//                    if (msg.what == 0) {
//                        VLog.e(
//                                "Tag",
//                                "当前进度: "
//                                        + ((int) (msg.arg1 * 100f / msg.arg2))
//                                        + "%"
//                                        + "status: "
//                                        + msg.obj);
//                    }
//                }
//            };
//
//    class DownloadContentObserver extends ContentObserver {
//        DownloadContentObserver() {
//            super(handler);
//        }
//
//        @Override
//        public void onChange(boolean selfChange) {
//            updateView();
//        }
//    }
//
//    private void updateView() {
//        int[] bytesAndStatus = getBytesAndStatus(mDownloadId);
//        int currentSize = bytesAndStatus[0]; // 当前大小
//        int totalSize = bytesAndStatus[1]; // 总大小
//        int status = bytesAndStatus[2]; // 下载状态
//        Message.obtain(handler, 0, currentSize, totalSize, status).sendToTarget();
//    }
//
//    private int[] getBytesAndStatus(long downloadId) {
//        int[] bytesAndStatus = new int[] {-1, -1, 0};
//        DownloadManager.Query query = new DownloadManager.Query().setFilterById(downloadId);
//        try (Cursor c = mDownloadManager.query(query)) {
//            if (c != null && c.moveToFirst()) {
//                bytesAndStatus[0] =
//                        c.getInt(
//                                c.getColumnIndexOrThrow(
//                                        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
//                bytesAndStatus[1] =
//                        c.getInt(c.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
//                bytesAndStatus[2] = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            }
//        } catch (Exception ignored) {
//        }
//        return bytesAndStatus;
//    }
//
//    // 检查下载状态
//    private void checkStatus() {
//        DownloadManager.Query query = new DownloadManager.Query();
//        // 通过下载的id查找
//        query.setFilterById(mDownloadId);
//        Cursor c = mDownloadManager.query(query);
//        if (c.moveToFirst()) {
//            int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
//            switch (status) {
//                    // 下载暂停
//                case DownloadManager.STATUS_PAUSED:
//                    VLog.e("Tag", "checkStatus: 下载暂停");
//                    break;
//                    // 下载延迟
//                case DownloadManager.STATUS_PENDING:
//                    VLog.e("Tag", "checkStatus: 下载延迟");
//                    break;
//                    // 正在下载
//                case DownloadManager.STATUS_RUNNING:
//                    VLog.e("Tag", "checkStatus: 下载中");
//                    break;
//                    // 下载完成
//                case DownloadManager.STATUS_SUCCESSFUL:
//                    VLog.e("Tag", "checkStatus: 下载完成");
//                    installAPK();
//                    break;
//                    // 下载失败
//                case DownloadManager.STATUS_FAILED:
//                    VLog.e("Tag", "checkStatus: 下载失败");
//                    break;
//            }
//        }
//        c.close();
//    }
//
//    // 下载到本地后执行安装
//    private void installAPK() {
//        VLog.e("Tag", "installAPK: 安装阶段");
//        File file = new File(mFilePath, mAPKName);
//        VLog.e("Tag", "installAPK: 文件是否存在" + file.exists());
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
//            Intent intent = new Intent(Intent.ACTION_VIEW);
//            Uri uri = Uri.fromFile(file);
//            VLog.e("Tag", "installAPK: " + uri.toString());
//
//            intent.setDataAndType(uri, "application/vnd.android.package-archive");
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            mActivity.startActivity(intent);
//        } else {
//            // 获取下载文件的Uri
//            Uri downloadFileUri = mDownloadManager.getUriForDownloadedFile(mDownloadId);
//            VLog.e("Tag", "7.0以上系统安装包的uri: " + downloadFileUri);
//            if (downloadFileUri != null) {
//                Intent intent = new Intent(Intent.ACTION_VIEW);
//                intent.setDataAndType(downloadFileUri, "application/vnd.android.package-archive");
//                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
//                mActivity.startActivity(intent);
//            }
//        }
//        Plat.app.unregisterReceiver(receiver);
//    }
//}
