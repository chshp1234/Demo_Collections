package com.example.aidltest.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import com.blankj.utilcode.util.Utils;

public final class ClipboardUtils {

    private static final Handler CLIPBOARD_HANDLER = new Handler(Looper.getMainLooper());

    private ClipboardUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    public static void copyText(final CharSequence text) {
        ClipboardManager cm =
                (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        cm.setPrimaryClip(ClipData.newPlainText("text", text));
    }

    /**
     * 复制文本到剪贴板
     *
     * @param text 文本
     */
    public static void copyTextAsync(final CharSequence text) {

        CLIPBOARD_HANDLER.post(
                new Runnable() {
                    @Override
                    public void run() {
                        ClipboardManager cm =
                                (ClipboardManager)
                                        Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
                        //noinspection ConstantConditions
                        cm.setPrimaryClip(ClipData.newPlainText("text", text));
                    }
                });
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    public static CharSequence getText() {
        ClipboardManager cm =
                (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        ClipData clip = cm.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).coerceToText(Utils.getApp());
        }
        return null;
    }

    /**
     * 获取剪贴板的文本
     *
     * @return 剪贴板的文本
     */
    public static void getTextAsync(Callback<CharSequence> callback) {
        CLIPBOARD_HANDLER.post(
                new Runnable() {
                    @Override
                    public void run() {
                        if (callback != null) {
                            ClipboardManager cm =
                                    (ClipboardManager)
                                            Utils.getApp()
                                                    .getSystemService(Context.CLIPBOARD_SERVICE);
                            //noinspection ConstantConditions
                            ClipData clip = cm.getPrimaryClip();
                            if (clip != null && clip.getItemCount() > 0) {
                                callback.call(clip.getItemAt(0).coerceToText(Utils.getApp()));
                            } else {
                                callback.call(null);
                            }
                        }
                    }
                });
    }

    /**
     * 复制uri到剪贴板
     *
     * @param uri uri
     */
    public static void copyUri(final Uri uri) {
        ClipboardManager cm =
                (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        cm.setPrimaryClip(ClipData.newUri(Utils.getApp().getContentResolver(), "uri", uri));
    }

    /**
     * 获取剪贴板的uri
     *
     * @return 剪贴板的uri
     */
    public static Uri getUri() {
        ClipboardManager cm =
                (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        ClipData clip = cm.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).getUri();
        }
        return null;
    }

    /**
     * 复制意图到剪贴板
     *
     * @param intent 意图
     */
    public static void copyIntent(final Intent intent) {
        ClipboardManager cm =
                (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        cm.setPrimaryClip(ClipData.newIntent("intent", intent));
    }

    /**
     * 获取剪贴板的意图
     *
     * @return 剪贴板的意图
     */
    public static Intent getIntent() {
        ClipboardManager cm =
                (ClipboardManager) Utils.getApp().getSystemService(Context.CLIPBOARD_SERVICE);
        //noinspection ConstantConditions
        ClipData clip = cm.getPrimaryClip();
        if (clip != null && clip.getItemCount() > 0) {
            return clip.getItemAt(0).getIntent();
        }
        return null;
    }

    public interface Callback<T> {
        void call(T text);
    }
}
