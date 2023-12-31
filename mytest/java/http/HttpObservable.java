package com.example.aidltest.http;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.http.exception.HttpResultFunction;
import com.google.gson.Gson;
import com.trello.rxlifecycle3.LifecycleProvider;
import com.trello.rxlifecycle3.android.ActivityEvent;
import com.trello.rxlifecycle3.android.FragmentEvent;

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * 适用Retrofit网络请求Observable(被监听者)
 *
 * @author csp
 * @date 2017/9/23
 */
public class HttpObservable {
    /**
     * 获取被监听者 备注:网络请求Observable构建 data:网络请求参数
     *
     * <h1>补充说明</h1>
     *
     * 无管理生命周期,容易导致内存溢出
     */
    public static <T> Observable<T> getObservable(Observable<T> apiObservable) {
        // showLog(request);
        Observable<T> observable =
                apiObservable
                        .onErrorResumeNext(new HttpResultFunction<>())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
        return observable;
    }

    /**
     * 获取被监听者 备注:网络请求Observable构建 data:网络请求参数.
     *
     * <p>同步请求
     *
     * <p>无管理生命周期,容易导致内存溢出
     *
     * @param apiObservable the api observable
     * @return the observable sync
     */
    public static <T> Observable<T> getObservableSync(Observable<T> apiObservable) {
        // showLog(request);
        return apiObservable.onErrorResumeNext(new HttpResultFunction<>());
    }

    /**
     * 获取被监听者 备注:网络请求Observable构建 data:网络请求参数
     *
     * <h1>补充说明</h1>
     *
     * 传入LifecycleProvider自动管理生命周期,避免内存溢出 备注:需要继承RxActivity.../RxFragment...
     */
    public static <T> Observable<T> getObservable(
            Observable<T> apiObservable, LifecycleProvider<ActivityEvent> lifecycle) {
        // showLog(request);
        Observable<T> observable;

        if (lifecycle != null) {
            // 随生命周期自动管理.eg:onCreate(start)->onStop(end)

            observable =
                    apiObservable
                            // 需要在这个位置添加
                            .compose(lifecycle.bindToLifecycle())
                            .onErrorResumeNext(new HttpResultFunction<>())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
        } else {
            observable = getObservable(apiObservable);
        }
        return observable;
    }

    /**
     * 获取被监听者 备注:网络请求Observable构建 data:网络请求参数
     *
     * <h1>补充说明</h1>
     *
     * 传入LifecycleProvider<ActivityEvent>手动管理生命周期,避免内存溢出
     * 备注:需要继承RxActivity,RxAppCompatActivity,RxFragmentActivity
     */
    public static <T> Observable<T> getObservable(
            Observable<T> apiObservable,
            LifecycleProvider<ActivityEvent> lifecycle,
            ActivityEvent event) {
        // showLog(request);
        Observable<T> observable;
        if (lifecycle != null) {
            // 手动管理移除监听生命周期.eg:ActivityEvent.STOP
            observable =
                    apiObservable
                            // 需要在这个位置添加
                            .compose(lifecycle.bindUntilEvent(event))
                            .onErrorResumeNext(new HttpResultFunction<>())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
        } else {
            observable = getObservable(apiObservable);
        }
        return observable;
    }

    /**
     * 获取被监听者 备注:网络请求Observable构建 data:网络请求参数
     *
     * <h1>补充说明</h1>
     *
     * 传入LifecycleProvider<FragmentEvent>手动管理生命周期,避免内存溢出 备注:需要继承RxFragment,RxDialogFragment
     */
    public static <T> Observable<T> getObservable(
            Observable<T> apiObservable,
            LifecycleProvider<FragmentEvent> lifecycle,
            FragmentEvent event) {
        //  showLog(request);
        Observable<T> observable;
        if (lifecycle != null) {
            // 手动管理移除监听生命周期.eg:FragmentEvent.STOP
            observable =
                    apiObservable
                            // 需要在这个位置添加
                            .compose(lifecycle.bindUntilEvent(event))
                            .onErrorResumeNext(new HttpResultFunction<>())
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread());
        } else {
            observable = getObservable(apiObservable);
        }
        return observable;
    }

    /** 打印log */
    private static void showLog(Map<String, Object> request) {
        if (request == null || request.size() == 0) {
            LogUtils.e("[http request]:");
        }
        LogUtils.e("[http request]:" + new Gson().toJson(request));
    }

    private void test(){
        /*int a = 1;
        int b = 2;
        int c = a + b;
        int c = a * b;*/
    }
}
