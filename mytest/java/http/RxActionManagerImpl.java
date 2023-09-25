package com.example.aidltest.http;

import android.annotation.TargetApi;
import android.os.Build;

import androidx.collection.ArrayMap;
import io.reactivex.disposables.Disposable;

/**
 * @author csp
 * @date 2017/9/23
 */
public class RxActionManagerImpl implements RxActionManager<Object> {
    private static volatile RxActionManagerImpl mInstance;
    /** 处理,请求列表 */
    private ArrayMap<Object, Disposable> mMaps;

    public static RxActionManagerImpl getInstance() {
        if (mInstance == null) {
            synchronized (RxActionManagerImpl.class) {
                if (mInstance == null) {
                    mInstance = new RxActionManagerImpl();
                }
            }
        }
        return mInstance;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    private RxActionManagerImpl() {
        mMaps = new ArrayMap<>();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void add(Object tag, Disposable disposable) {
        mMaps.put(tag, disposable);
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void remove(Object tag) {
        if (!mMaps.isEmpty()) {
            mMaps.remove(tag);
        }
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void cancel(Object tag) {
        if (mMaps.isEmpty()) {
            return;
        }
        if (mMaps.get(tag) == null) {
            return;
        }
        if (!mMaps.get(tag).isDisposed()) {
            mMaps.get(tag).dispose();
        }
        mMaps.remove(tag);
    }

    /**
     * 判断是否取消了请求
     *
     * @param tag 标识
     * @return isDisposed
     */
    public boolean isDisposed(Object tag) {
        if (mMaps.isEmpty() || mMaps.get(tag) == null) {
            return true;
        }
        return mMaps.get(tag).isDisposed();
    }
}
