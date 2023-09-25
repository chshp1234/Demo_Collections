package com.example.aidltest.base.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.R;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;

import java.lang.reflect.Field;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @author csp
 * @date 2017/9/20
 */
public abstract class BaseActivity extends RxAppCompatActivity {

    protected final String TAG = getClass().getSimpleName();
    protected ImageView mToolbarBack;
    protected TextView mToolbarTitle;
    protected TextView mToolbarSubTitle;
    protected ImageView mToolbarSubTitleImage;
    protected Toolbar toolBar;
    protected boolean isShowSubTitle = false;
    protected boolean isShowSubTitleImage = false;
    protected boolean isShowTitle = true;
    protected boolean isShowBack = true;
    protected boolean isShowToolBar = true;

    public ProgressDialog dialog;

    protected Context mContext;
    protected Unbinder unBinder;
    private InputMethodManager imm;
    /** 回调函数 */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // 去除半透明状态栏
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 一般配合fitsSystemWindows()使用,
        // 或者在根部局加上属性android:fitsSystemWindows="true", 使根部局全屏显示
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        //        getWindow().setStatusBarColor(Color.TRANSPARENT);
        super.onCreate(savedInstanceState);
        LogUtils.v(getClass().getSimpleName().intern() + "：onCreate");
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
        }
        //        getWindow().setBackgroundDrawable(new ColorDrawable(0));
        unBinder = ButterKnife.bind(this);
        if (isInitStateBarEnabled()) {
            initStateBar();
        }
        mContext = this;
        initDialog();
        initToolBar();
        initData();
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        LogUtils.v(getClass().getSimpleName().intern() + "：onRestart");
    }

    @Override
    protected void onStart() {
        super.onStart();
        LogUtils.v(getClass().getSimpleName().intern() + "：onStart");
        showToolBar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtils.v(getClass().getSimpleName().intern() + "：onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.v(getClass().getSimpleName().intern() + "：onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtils.v(getClass().getSimpleName().intern() + "：onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogUtils.v(getClass().getSimpleName().intern() + "：onDestroy");
        // 移除view绑定
        if (unBinder != null) {
            unBinder.unbind();
        }
        this.imm = null;

        /*
         在退出使用InputMethodManager的Activity时，调用fixFocusedViewLeak方法解决Android InputMethodManager
         导致的内存泄露
        */
        //        FixInputMethodManagerLeaksUtils.fixFocusedViewLeak(getApplication());
        fixInputMethodManagerLeak(mContext);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        LogUtils.v(getClass().getSimpleName().intern() + "：onNewIntent");
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_SCREEN_CODE:
                if (resultCode != Activity.RESULT_OK) {
                    ToastUtils.showShort("授权截屏失败");
                    return;
                }
                ScreenShotUtils.getInstance().init(data);

                break;
            case REQUEST_ACCESSIBILITY_CODE:
                if (!AccessibilityCheckUtils.isAccessibilityEnable(
                        getApplicationContext(),
                        AppUtils.getAppPackageName()
                                + "/"
                                + AutoScriptService.class.getCanonicalName())) {
                    ToastUtils.showShort("授权无障碍服务失败");
                }
                break;
            case REQUEST_FLOATING_CODE:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (!Settings.canDrawOverlays(this)) {
                        ToastUtils.showShort("授权悬浮窗失败");
                    } else {
                        ToastUtils.showShort("授权悬浮窗成功");
                        startService(new Intent(this, FloatWindowService.class));
                    }
                }
                break;
            default:
                break;
        }
    }*/

    /**
     * 防止inputMethodManager造成的内存泄漏
     *
     * @param destContext
     */
    public static void fixInputMethodManagerLeak(Context destContext) {
        if (destContext == null) {
            return;
        }

        InputMethodManager imm =
                (InputMethodManager) destContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }

        String[] arr = new String[] {"mCurRootView", "mServedView", "mNextServedView"};
        Field f;
        Object objGet;
        for (String param : arr) {
            try {
                f = imm.getClass().getDeclaredField(param);
                if (!f.isAccessible()) {
                    f.setAccessible(true);
                } // author: sodino mail:sodino@qq.com
                objGet = f.get(imm);
                if (objGet != null && objGet instanceof View) {
                    View vGet = (View) objGet;
                    // 被InputMethodManager持有引用的context是想要目标销毁的
                    if (vGet.getContext() == destContext) {
                        // 置空，破坏掉path to gc节点
                        f.set(imm, null);
                    } else {
                        // 不是想要目标销毁的，即为又进了另一层界面了，不要处理，避免影响原逻辑,也就不用继续for循环了
                        break;
                    }
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
    }

    protected void initStateBar() {
        // 在BaseActivity里初始化
        //        mImmersionBar = ImmersionBar.with(this);
        //        mImmersionBar.statusBarColor(R.color.white).statusBarDarkFont(true, 0.2f).init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow()
                    .getDecorView()
                    .setSystemUiVisibility(
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            //            BarUtils.setStatusBarColor(this, ContextCompat.getColor(this,
            // R.color.white), 0, true);
        } else {
            BarUtils.setStatusBarColor(
                    this, ContextCompat.getColor(this, R.color.translucence_white), false);
        }
    }

    /**
     * 是否可以使用沉浸式 Is bar enabled boolean.
     *
     * @return the boolean
     */
    protected boolean isInitStateBarEnabled() {
        return true;
    }

    public void hideSoftKeyBoard() {
        View localView = getCurrentFocus();
        if (this.imm == null) {
            this.imm = ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE));
        }
        if ((localView != null) && (this.imm != null)) {
            this.imm.hideSoftInputFromWindow(localView.getWindowToken(), 2);
        }
    }

    @Override
    public void finish() {
        super.finish();
        hideSoftKeyBoard();
    }

    void initDialog() {
        dialog = new ProgressDialog(mContext, R.style.customProgressBarAlert);
        dialog.setMessage("");
        dialog.setCanceledOnTouchOutside(true);
    }

    void initToolBar() {

        toolBar = findViewById(R.id.toolbar);
        mToolbarBack = findViewById(R.id.toolbar_back);
        mToolbarTitle = findViewById(R.id.toolbar_title);
        mToolbarSubTitle = findViewById(R.id.toolbar_subtitle);
        mToolbarSubTitleImage = findViewById(R.id.toolbar_subtitle_image);
        setSupportActionBar(toolBar);
        if (mToolbarTitle != null) {
            // getTitle()的值是activity的android:lable属性值
            mToolbarTitle.setText(getTitle());
            // 设置默认的标题不显示
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    /**
     * 获取头部标题的TextView
     *
     * @return
     */
    public TextView getToolbarTitle() {
        return mToolbarTitle;
    }

    /**
     * 获取头部子标题的TextView
     *
     * @return
     */
    public TextView getSubTitle() {
        return mToolbarSubTitle;
    }

    /**
     * 设置头部标题
     *
     * @param title
     */
    public void setToolBarTitle(CharSequence title) {
        mToolbarTitle.setText(title);
    }

    /**
     * 设置头部标题颜色
     *
     * @param color
     */
    public void setToolBarTitleColor(int color) {
        if (mToolbarTitle != null) {
            mToolbarTitle.setTextColor(color);
        }
    }

    /**
     * 设置头部子标题
     *
     * @param title
     */
    public void setToolBarSubTitle(CharSequence title) {
        if (mToolbarSubTitle != null) {
            mToolbarSubTitle.setText(title);
        }
    }

    /**
     * 设置头部子标题颜色
     *
     * @param color
     */
    public void setToolBarSubTitleColor(int color) {
        if (mToolbarSubTitle != null) {
            mToolbarSubTitle.setTextColor(color);
        }
    }

    public void setToolBarColor(int color) {
        toolBar.setBackgroundColor(color);
    }

    /**
     * 设置后退按钮图片
     *
     * @return
     */
    public void setBackImage(int resId) {
        mToolbarBack.setImageResource(resId);
    }

    /** 设置头部子标题图片 */
    public void setToolBarSubImage(int resources) {
        mToolbarSubTitleImage.setImageResource(resources);
    }

    /** 设置显示toolbar */
    public void showToolBar() {
        if (null != toolBar) {
            if (isShowToolBar()) {
                toolBar.setVisibility(View.VISIBLE);
                showBack();
                showTitle();
                showSubTitle();
                showSubTitleImage();
            } else {
                toolBar.setVisibility(View.GONE);
            }
        } else {

        }
    }

    /** 显示头部子标题 */
    public void showSubTitle() {
        if (isShowSubTitle()) {
            mToolbarSubTitle.setVisibility(View.VISIBLE);
        } else {
            mToolbarSubTitle.setVisibility(View.GONE);
        }
    }

    /** 显示头部子标题图片按钮 */
    public void showSubTitleImage() {
        if (isShowSubTitleImage()) {
            mToolbarSubTitleImage.setVisibility(View.VISIBLE);
        } else {
            mToolbarSubTitleImage.setVisibility(View.GONE);
        }
    }

    /** 显示后退按钮 */
    public void showBack() {
        if (isShowBack()) {
            mToolbarBack.setVisibility(View.VISIBLE);
            mToolbarBack.setOnClickListener(view -> onBackPressed());

        } else {
            mToolbarBack.setVisibility(View.GONE);
        }
    }

    /** 显示主标题 */
    public void showTitle() {
        if (isShowTitle()) {
            mToolbarTitle.setVisibility(View.VISIBLE);
        } else {
            mToolbarTitle.setVisibility(View.GONE);
        }
    }

    /**
     * 是否显示toolbar
     *
     * @return
     */
    public boolean isShowToolBar() {
        return isShowToolBar;
    }

    /**
     * 是否显示后退按钮,默认显示,可在子类重写该方法.
     *
     * @return
     */
    public boolean isShowBack() {
        return isShowBack;
    }

    /**
     * 是否显示头部子标题
     *
     * @return
     */
    protected boolean isShowSubTitle() {
        return isShowSubTitle;
    }

    /**
     * 是否显示头部子标题图片按钮
     *
     * @return
     */
    protected boolean isShowSubTitleImage() {
        return isShowSubTitleImage;
    }

    /**
     * 是否显示标题
     *
     * @return
     */
    protected boolean isShowTitle() {
        return isShowTitle;
    }

    public void setShowSubTitle(boolean showSubTitle) {
        isShowSubTitle = showSubTitle;
    }

    public void setShowSubTitleImage(boolean showSubTitleImage) {
        isShowSubTitleImage = showSubTitleImage;
    }

    public void setShowTitle(boolean showTitle) {
        isShowTitle = showTitle;
    }

    public void setShowBack(boolean showBack) {
        isShowBack = showBack;
    }

    public void setShowToolBar(boolean showToolBar) {
        isShowToolBar = showToolBar;
    }

    /** 初始化数据 */
    protected abstract void initData();

    /** 初始化控件 */
    protected abstract void initView();

    /**
     * 获取显示view的xml文件ID
     *
     * @return xml文件ID
     */
    protected abstract int getLayoutId();
}
