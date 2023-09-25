package com.example.aidltest.base.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import butterknife.ButterKnife;

/**
 * viewpager+fragment懒加载
 *
 * @author csp
 * @date 2017/12/21
 */
public abstract class LazyFragment extends BaseFragment {

    /** 视图是否已经初初始化 */
    protected boolean isInit = false;

    protected boolean isLoad = false;
    protected final String TAG = "LazyLoadFragment";
    private View view;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(getContentViewId(), container, false);
        isInit = true;
        unBinder = ButterKnife.bind(this, view);
        /** 初始化的时候去加载数据* */
        isCanLoadData();
        return view;
    }

    @Override
    protected void init(View view) {}

    /** 视图是否已经对用户可见，系统的方法 */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        isCanLoadData();
    }

    /** 是否可以加载数据 可以加载数据的条件： 1.视图已经初始化 2.视图对用户可见 */
    private void isCanLoadData() {
        if (!isInit) {
            return;
        }

        if (!isLoad && getUserVisibleHint()) {
            lazyLoad();
            isLoad = true;
        } else {
            if (isLoad) {
                stopLoad();
            }
        }
    }

    /** 视图销毁的时候讲Fragment是否初始化的状态变为false */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        isInit = false;
        isLoad = false;
    }

    /**
     * 获取设置的布局
     *
     * @return
     */
    protected View getContentView() {
        return view;
    }

    /**
     * 找出对应的控件
     *
     * @param id
     * @param <T>
     * @return
     */
    protected <T extends View> T findViewById(int id) {

        return (T) getContentView().findViewById(id);
    }

    /** 当视图初始化并且对用户可见的时候去真正的加载数据 */
    protected abstract void lazyLoad();

    /** 当视图已经对用户不可见并且加载过数据，如果需要在切换到其他页面时停止加载数据，可以覆写此方法 */
    protected void stopLoad() {}
}
