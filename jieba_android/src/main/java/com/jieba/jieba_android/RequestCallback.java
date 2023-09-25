package com.jieba.jieba_android;

/**
 * Created by JackMeGo on 2017/7/5.
 */

public interface RequestCallback<E> {
    void onSuccess(E result);

    default void onError(String errorMsg) {
    }
}
