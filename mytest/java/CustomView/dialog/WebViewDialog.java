package com.example.aidltest.CustomView.dialog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import com.example.aidltest.CustomView.webview.WebViewUi;
import com.example.aidltest.base.activity.BaseDialogFragment;

public class WebViewDialog extends BaseDialogFragment {

    WebViewUi webViewUi;

    public WebViewDialog(FragmentActivity context) {
        super(context);
    }

    @Override
    public View getContentViewId(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        webViewUi = new WebViewUi(mContext);
        return webViewUi;
    }

    @Override
    public void initView(View view, @Nullable Bundle savedInstanceState) {
        webViewUi.goUrl("https://www.baidu.com/");
    }
}
