package com.example.aidltest;

import android.webkit.WebView;
import android.widget.FrameLayout;

import androidx.core.text.HtmlCompat;

import com.blankj.utilcode.util.BarUtils;
import com.example.aidltest.base.BaseActivity;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;

import butterknife.BindView;

import static androidx.core.text.HtmlCompat.FROM_HTML_MODE_LEGACY;

public class WebActivity extends BaseActivity {
    @BindView(R.id.web_root)
    FrameLayout webRoot;

    private AgentWeb mAgentWeb;

    @Override
    protected void initData() {}

    @Override
    protected void initView() {
        //        web.loadUrl("csp://csp:8080/mytest?query1=1&query2=true");
        BarUtils.setStatusBarColor(this, getColor(R.color.transparent));
        mAgentWeb =
                AgentWeb.with(this)
                        .setAgentWebParent(webRoot, new FrameLayout.LayoutParams(-1, -1))
                        .useDefaultIndicator(-1, 3)
                        .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                        .setOpenOtherPageWays(
                                DefaultWebClient.OpenOtherPageWays.DERECT) // 打开其他应用时，弹窗咨询用户是否前往其他应用
                        .createAgentWeb()
                        .ready()
                        .get();

        String content =
                HtmlCompat.fromHtml(
                                "<!DOCTYPE html><html>\n"
                                        + "\t<head></head>\n"
                                        + "\t\t<body>\n"
                                        + "\t\t\t<video width=\"100%\" height=\"240\" controls>\n"
                                        + "    \t\t\t<source src=\"http://9890.vod.myqcloud.com/9890_4e292f9a3dd011e6b4078980237cc3d3.f30.mp4\" type=\"video/mp4\">\n"
                                        + "    \t\t\t\t您的浏览器不支持 video 标签。\n"
                                        + "\t\t\t</video>\n"
                                        + "\t\t</body>\n"
                                        + "</html>",
                                FROM_HTML_MODE_LEGACY)
                        .toString();
//        mAgentWeb.getUrlLoader().loadData(content, "text/html", "UTF-8");
        mAgentWeb.getUrlLoader().loadUrl("file:///android_asset/test_video.html");
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_web;
    }
}
