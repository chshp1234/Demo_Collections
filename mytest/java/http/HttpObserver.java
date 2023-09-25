package com.example.aidltest.http;

import android.text.TextUtils;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.http.exception.ApiException;
import com.example.aidltest.http.exception.ExceptionEngine;

import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * 使用网络请求的过程中我们肯定会遇到取消请求的场景，这里我们实现一个HttpRequestListener，
 * 为每一个请求添加唯一的TAG用来标识具体的每一个请求，
 * 开始请求时保存TAG，请求成功/失败移除标志，同时TAG也用做取消请求的标志。
 * 适用Retrofit网络请求Observer(监听者)
 *
 * @author csp
 * @date 2017/9/23
 */
public abstract class HttpObserver<T> implements Observer<BaseBean<T>>, HttpRequestListener {
    /** 请求标识 */
    private String mTag;

    public HttpObserver() {}

    public HttpObserver(String tag) {
        this.mTag = tag;
    }

    @Override
    public void onError(Throwable e) {
        RxActionManagerImpl.getInstance().remove(mTag);

        if (e instanceof ApiException) {
            LogUtils.e(((ApiException) e).getCode() + "：" + ((ApiException) e).getMsg());
            onFail(((ApiException) e).getCode(), ((ApiException) e).getMsg());
        } else {
            onError(new ApiException(e, ExceptionEngine.UNKNOWN_ERROR));
        }
    }

    @Override
    public void onComplete() {}

    @Override
    public void onNext(@NonNull BaseBean<T> BaseBean) {

        if (!TextUtils.isEmpty(mTag)) {
            RxActionManagerImpl.getInstance().remove(mTag);
        }
        if (BaseBean.getReturnValue() == 1) {
            T t = BaseBean.getData();
            onSuccess(t);
        } else {
            onError(new ApiException(BaseBean.getReturnValue(), BaseBean.getErrMsg()));
        }

    }

    @Override
    public void onSubscribe(@NonNull Disposable d) {
        if (!TextUtils.isEmpty(mTag)) {
            RxActionManagerImpl.getInstance().add(mTag, d);
        }
        onStart(d);
    }

    @Override
    public void cancel() {
        if (!TextUtils.isEmpty(mTag)) {
            RxActionManagerImpl.getInstance().cancel(mTag);
        }
    }

    /**
     * Cancel. 取消
     *
     * @param tag the tag
     */
    public static void cancel(String tag) {
        if (!TextUtils.isEmpty(tag)) {
            RxActionManagerImpl.getInstance().cancel(tag);
        }
    }

    /** 是否已经处理 */
    public boolean isDisposed() {
        if (TextUtils.isEmpty(mTag)) {
            return true;
        }
        return RxActionManagerImpl.getInstance().isDisposed(mTag);
    }

    public String getmTag() {
        return mTag;
    }

    public void setmTag(String mTag) {
        this.mTag = mTag;
    }

    /**
     * 请求数据成功 且响应码为200
     *
     * @param response 服务器返回的数据
     */
    public abstract void onSuccess(T response);

    /**
     * 请求数据失败
     *
     * @param code 失败代码
     * @param msg 失败信息
     */
    public abstract void onFail(int code, String msg);

    /**
     * 请求数据成功开始
     *
     * @param disposable disposable
     */
    public abstract void onStart(Disposable disposable);
}
