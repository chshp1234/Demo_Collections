package com.example.aidltest;

import com.example.aidltest.base.BaseActivity;
import com.example.aidltest.bean.AddressBean;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ScreenActivity extends BaseActivity {

    public static AddressBean addressBeanStatic = new AddressBean();

    @Override
    protected void initData() {

        AddressBean addressBean1 = new AddressBean();
        addressBeanStatic = addressBean1;

        AddressBean addressBean2 = new AddressBean();
        addressBeanStatic = addressBean2;

        EventBus.getDefault().post(new RefreshDataEvent());
    }

    @Override
    protected void initView() {
        refreshData(null);
    }

    @Override
    protected int getLayoutId() {
        return 0;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshData(RefreshDataEvent refreshDataEvent){

    }

    static class RefreshDataEvent{

    }
}
