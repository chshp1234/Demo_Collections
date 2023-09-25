package com.example.aidltest.base.activity;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.LogUtils;
import com.trello.rxlifecycle3.components.support.RxDialogFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseDialogFragment extends RxDialogFragment {

    protected FragmentActivity mContext;
    protected Unbinder unBinder;

    public BaseDialogFragment(FragmentActivity context) {
        this.mContext = context;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        // 加这句话去掉自带的标题栏
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);

        return getContentViewId(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        unBinder = ButterKnife.bind(this, view);
        initView(view, savedInstanceState);
    }

    public abstract View getContentViewId(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState);

    public abstract void initView(View view, @Nullable Bundle savedInstanceState);

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(false);
        /** 设置宽度全屏，要设置在show的后面 */
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        //        dialog.getWindow().getDecorView().setPadding(0, 0, 0, 0);
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.getWindow().setAttributes(layoutParams);
    }

    public void show() {
        show(mContext.getSupportFragmentManager(), "AdAlertDialog");
    }

    @Override
    public void show(@NonNull FragmentManager manager, String tag) {
        try {
            super.show(manager, tag);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    @Override
    public void dismiss() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(BaseDialogFragment.this::dismiss);
            LogUtils.e("dialog dismiss error!");
            return;
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            LogUtils.e("dismiss exception, e = " + e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // 移除view绑定
        if (unBinder != null) {
            unBinder.unbind();
        }
    }
}
