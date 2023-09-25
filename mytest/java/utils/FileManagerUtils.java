package com.example.aidltest.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;

import com.blankj.utilcode.util.FileUtils;
import com.blankj.utilcode.util.ImageUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.UriUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.example.aidltest.MyApplication;
import com.example.aidltest.base.GlideApp;

import java.io.File;

import butterknife.Action;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

import static com.example.aidltest.config.FilePathConfig.APP_PIC;
import static com.example.aidltest.config.FilePathConfig.EXTERNAL_STORAGE_PATH;

public class FileManagerUtils {
    public static void scanFile(Context context, String[] path, String[] mime) {
        MediaScannerConnection.scanFile(
                context,
                path,
                mime,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        LogUtils.d("onScanCompleted: " + path);

                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        intent.setData(uri);
                        context.sendBroadcast(intent);
                    }
                });
    }

    public static void saveImg(String path, Bitmap bitmap) {
        if (StringUtils.isTrimEmpty(path) || bitmap == null) {
            return;
        }
        String destPath = path + File.separator + "temp.jpg";
        ImageUtils.save(bitmap, destPath, Bitmap.CompressFormat.JPEG);
        scanFile(MyApplication.getContext(), new String[] {destPath}, new String[] {"image/*"});
    }

    public static void saveImg(String path, Uri uri) {
        if (StringUtils.isTrimEmpty(path) || uri == null) {
            return;
        }
        File src = UriUtils.uri2File(uri);
        LogUtils.d("disposePicEvent: src=" + src.getAbsolutePath());
        LogUtils.d("disposePicEvent: srcName=" + src.getName());
        String destPath =
                path
                        + File.separator
                        + "temp"
                        + src.getName().substring(src.getName().indexOf("."));
        FileUtils.copy(src.getAbsolutePath(), destPath);

        scanFile(MyApplication.getContext(), new String[] {destPath}, new String[] {"image/*"});
    }

    public static void saveImg(String path, String picPath) {
        if (StringUtils.isTrimEmpty(path) || StringUtils.isTrimEmpty(picPath)) {
            return;
        }
        File src = new File(picPath);
        LogUtils.d("disposePicEvent: src=" + src.getAbsolutePath());
        LogUtils.d("disposePicEvent: srcName=" + src.getName());
        String destPath =
                path
                        + File.separator
                        + "temp"
                        + src.getName().substring(src.getName().indexOf("."));
        FileUtils.copy(src.getAbsolutePath(), destPath);

        scanFile(MyApplication.getContext(), new String[] {destPath}, new String[] {"image/*"});
    }

    public static void saveImgs(String path, String... picPath) {
        if (StringUtils.isTrimEmpty(path) || picPath == null || picPath.length <= 0) {
            return;
        }

        String[] destPaths = new String[picPath.length];
        String[] mimes = new String[picPath.length];
        String destPath;
        String srcName;
        for (int i = 0, l = picPath.length; i < l; i++) {
            File src = new File(picPath[i]);
            srcName = src.getName();
            LogUtils.d("disposePicEvent: src=" + src.getAbsolutePath());
            LogUtils.d("disposePicEvent: srcName=" + srcName);

            destPath =
                    path
                            + File.separator
                            + "temp_"
                            + srcName
                            + src.getName().substring(src.getName().lastIndexOf("."));
            FileUtils.copy(src.getAbsolutePath(), destPath);
            destPaths[i] = destPath;
            mimes[i] = "image/*";
        }

        scanFile(MyApplication.getContext(), destPaths, mimes);
    }

    public static void clearFile(String parent) {
        if (StringUtils.isTrimEmpty(parent)) {
            return;
        }
        File file = new File(parent);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (int i = 0, l = files.length; i < l; i++) {

                    FileUtils.delete(files[i]);

                    FileManagerUtils.scanFile(
                            MyApplication.getContext(),
                            new String[] {files[i].getAbsolutePath()},
                            new String[] {"image/*"});
                }
            }
        }
    }

    // 保存网络图片到相册
    public static void saveNetImageUrlToGallery(String url) {
        Observable.just(url)
                .map(
                        (Function<String, File>)
                                s -> {
                                    File file =
                                            GlideApp.with(MyApplication.getContext())
                                                    .download(url)
                                                    .submit()
                                                    .get();

                                    LogUtils.d(
                                            "saveNetImageUrlToGallery: "
                                                    + UriUtils.file2Uri(file).toString());
                                    //
                                    // saveImg(EXTERNAL_STORAGE_PATH, file.getAbsolutePath());
                                    return file;
                                }

                        /*new Func1<String, File>() {
                            @Override
                            public File call(String s) {
                                File file = null;
                                try {
                                    file = Glide.with(context).load(s).downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                return file;
                            }
                        }*/ )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        file -> {
                            FileUtils.copy(
                                    file.getAbsolutePath(),
                                    APP_PIC
                                            + File.separator
                                            + "PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
                            LogUtils.d(
                                    "saveNetImageUrlToGallery: "
                                            + APP_PIC
                                            + File.separator
                                            + "PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");

                            ClipboardUtils.copyText(
                                    APP_PIC
                                            + File.separator
                                            + "PCtm_d9c8750bed0b3c7d089fa7d55720d6cf.png");
                        });
    }
}
