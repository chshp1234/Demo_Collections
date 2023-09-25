package com.example.aidltest.base;

import android.content.Context;
import android.os.Bundle;

import com.example.aidltest.utils.ActivityStackManager;
import com.trello.rxlifecycle3.components.support.RxFragmentActivity;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author csp
 * @date 2017/9/23
 */
public abstract class BaseFragmentActivity extends RxFragmentActivity {
    protected Context mContext;
    protected Unbinder unBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityStackManager.getInstance().addActivity(this);
        setContentView(getContentViewId());
        mContext = this;
        unBinder = ButterKnife.bind(this);
        init();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除view绑定
        if (unBinder != null) {
            unBinder.unbind();
        }
        ActivityStackManager.getInstance().removeActivity(this);
    }

    /**
     * 获取显示view的xml文件ID
     *
     * @return xml文件ID
     */
    protected abstract int getContentViewId();

    /**
     * 初始化应用程序，设置一些初始化数据,获取数据等操作
     */
    protected abstract void init();

}
