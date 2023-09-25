package com.example.aidltest.base;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.blankj.utilcode.util.LogUtils;
import com.trello.rxlifecycle3.components.support.RxFragment;

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
        LogUtils.a(
                getClass().getSimpleName()
                        + "：onAttach"
                        + "--------"
                        + activity.getClass().getSimpleName());
        mContext = (T) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.a(getClass().getSimpleName() + "：onCreate");
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(getContentViewId(), null);
        unBinder = ButterKnife.bind(this, view);
        //        mContext = (T) getActivity();
        LogUtils.a(getClass().getSimpleName() + "：onCreateView");
        init();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.a(getClass().getSimpleName() + "：onActivityCreated");
    }

    @Override
    public void onStart() {
        super.onStart();
        LogUtils.a(getClass().getSimpleName() + "：onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        LogUtils.a(getClass().getSimpleName() + "：onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        LogUtils.a(getClass().getSimpleName() + "：onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        LogUtils.a(getClass().getSimpleName() + "：onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LogUtils.a(getClass().getSimpleName() + "：onDestroyView");
        // 移除view绑定
        if (unBinder != null) {
            unBinder.unbind();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtils.a(getClass().getSimpleName() + "：onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        LogUtils.a(getClass().getSimpleName() + "：onDetach");
    }


    /**
     * 获取显示view的xml文件ID
     *
     * @return xml文件ID
     */
    protected abstract int getContentViewId();

    /** 初始化应用程序，设置一些初始化数据,获取数据等操作 */
    protected abstract void init();
}
