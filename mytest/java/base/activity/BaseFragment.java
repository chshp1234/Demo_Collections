package com.example.aidltest.base.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.trello.rxlifecycle3.components.support.RxFragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author csp
 * @date 2017/9/28
 */
public abstract class BaseFragment<T> extends RxFragment {
    protected T mContext;
    protected Unbinder unBinder;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        LogUtils.v(
                getClass().getSimpleName().intern()
                        + "：onAttach"
                        + "--------"
                        + activity.getClass().getSimpleName().intern());
        mContext = (T) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.v(getClass().getSimpleName().intern() + "：onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), null);
        unBinder = ButterKnife.bind(this, view);
        //        mContext = (T) getActivity();
        LogUtils.v(getClass().getSimpleName().intern() + "：onCreateView");
        init(view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.v(getClass().getSimpleName().intern() + "：onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.v(getClass().getSimpleName().intern() + "：onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.v(getClass().getSimpleName().intern() + "：onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.v(getClass().getSimpleName().intern() + "：onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.v(getClass().getSimpleName().intern() + "：onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.v(getClass().getSimpleName().intern() + "：onDestroyView");
        mContext = null;
        // 移除view绑定
        if (unBinder != null) {
            unBinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.v(getClass().getSimpleName().intern() + "：onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.v(getClass().getSimpleName().intern() + "：onDetach");
    }

    /**
     * 获取显示view的xml文件ID
     *
     * @return xml文件ID
     */
    protected abstract int getContentViewId();

    /** 初始化应用程序，设置一些初始化数据,获取数据等操作 */
    protected abstract void init(View view);
}
