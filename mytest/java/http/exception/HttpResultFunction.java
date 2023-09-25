package com.example.aidltest.http.exception;

import com.blankj.utilcode.util.LogUtils;

import io.reactivex.Observable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;

/**
 * 处理Retrofit抛出的Exception，通过ExceptionEngine转化为统一的错误类型ApiException
 *
 * @author csp
 * @date 2017/9/22
 */
public class HttpResultFunction<T> implements Function<Throwable, Observable<T>> {
    @Override
    public Observable<T> apply(@NonNull Throwable throwable) throws Exception {
        // 打印具体错误
        int i1 = 1;
        int i2 = 1;
        int i3 = 1;
        int i4 = 1;

        LogUtils.e("HttpResultFunction：" + throwable);
        return Observable.error(ExceptionEngine.handleException(throwable));
    }
}
