package com.example.aidltest.CustomView.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.StringUtils;
import com.example.aidltest.R;
import com.example.aidltest.utils.AccessUtils;

public class LovelyAlertDialog extends Dialog {
    protected ImageView bgTitle;
    protected TextView title;
    protected TextView msg;
    protected TextView description;
    protected View dividerTip;
    protected TextView tip;
    protected LinearLayout showTip;
    protected TextView btnCancel;
    protected TextView btnConfirm;
    protected Context mContext;
    protected LinearLayout dialogRoot;

    protected LovelyAlertDialog(@NonNull Context context) {
        super(context, R.style.lovelyAlertDialogStyle);
        this.mContext = context;
        dialogRoot = (LinearLayout) View.inflate(this.mContext, R.layout.dialog_lovely_alert, null);
        bgTitle = dialogRoot.findViewById(R.id.bg_title);
        title = dialogRoot.findViewById(R.id.title);
        msg = dialogRoot.findViewById(R.id.msg);
        description = dialogRoot.findViewById(R.id.description);
        dividerTip = dialogRoot.findViewById(R.id.divider_tip);
        tip = dialogRoot.findViewById(R.id.tip);
        showTip = dialogRoot.findViewById(R.id.show_tip);
        btnCancel = dialogRoot.findViewById(R.id.btn_cancel);
        btnConfirm = dialogRoot.findViewById(R.id.btn_confirm);
        setCanceledOnTouchOutside(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(this.dialogRoot);
    }

    public View getContentView() {
        return this.dialogRoot;
    }

    public void show() {
        try {
            if (AccessUtils.isCanDrawOverlays(mContext)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    getWindow().setType(WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY);
                } else {
                    getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                }
            }
            super.show();
            /** 设置宽度全屏，要设置在show的后面 */
            WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
            layoutParams.gravity = Gravity.CENTER;
            layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            getWindow().getDecorView().setPadding(SizeUtils.dp2px(25), 0, SizeUtils.dp2px(25), 0);
            getWindow().setAttributes(layoutParams);
        } catch (Exception e) {
            LogUtils.e(e);
        }
    }

    public void dismiss() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            new Handler(Looper.getMainLooper()).post(LovelyAlertDialog.this::dismiss);
            LogUtils.e("dialog dismiss error!");
            return;
        }
        try {
            super.dismiss();
        } catch (Exception e) {
            LogUtils.e("dismiss exception, e = " + e.getMessage());
        }
    }

    public void createDialog(DialogParam param) {
        setDialogTitle(param.contentTitle);
        setDialogMsg(param.contentMsg);
        setDialogDescription(param.contentDescription);
        setDialogTip(param.contentTip);
        setDialogConfirmBtn(param.contentConfirmBtn, param.onConfirmClickListener);
        setDialogCancelBtn(param.contentCancelBtn, param.onCancelClickListener);
        if (param.colorTitle != -1) {}
        if (param.colorMsg != -1) {}
        if (param.colorDescription != -1) {
            description.setTextColor(param.colorDescription);
        }
        if (param.colorTip != -1) {}
        if (param.colorConfirmBtn != -1) {}
        if (param.colorCancelBtn != -1) {}
        setCancelable(param.isCancelable);
        setCanceledOnTouchOutside(param.isCanceledOnTouchOutside);
        isShowConfirmBtn(param.showConfirmBtn);
        isShowCancelBtn(param.showCancelBtn);
        if (param.onDismissListener != null) {
            setOnDismissListener(param.onDismissListener);
        }
        if (param.onCancelListener != null) {
            setOnCancelListener(param.onCancelListener);
        }
        if (param.onKeyListener != null) {
            setOnKeyListener(param.onKeyListener);
        }
        if (!StringUtils.isTrimEmpty(param.bgTitle)) {}
    }

    public void setDialogCancelBtn(String contentCancelBtn, OnClickListener onCancelClickListener) {
        if (StringUtils.isTrimEmpty(contentCancelBtn)) {
            btnCancel.setText("取消");
        } else {
            btnCancel.setText(contentCancelBtn);
        }
        if (onCancelClickListener != null) {
            btnCancel.setOnClickListener(
                    v -> onCancelClickListener.onClick(LovelyAlertDialog.this, -2));
        } else {
            btnCancel.setOnClickListener(v -> LovelyAlertDialog.this.cancel());
        }
    }

    public void setDialogConfirmBtn(
            String contentConfirmBtn, OnClickListener onConfirmClickListener) {
        if (StringUtils.isTrimEmpty(contentConfirmBtn)) {
            btnConfirm.setText("确认");
        } else {
            btnConfirm.setText(contentConfirmBtn);
        }
        if (onConfirmClickListener != null) {
            btnConfirm.setOnClickListener(
                    v -> onConfirmClickListener.onClick(LovelyAlertDialog.this, -2));
        } else {
            btnConfirm.setOnClickListener(v -> LovelyAlertDialog.this.cancel());
        }
    }

    public void setDialogTip(String contentTip) {
        if (StringUtils.isTrimEmpty(contentTip)) {
            showTip.setVisibility(View.GONE);
        }
        tip.setText(contentTip);
    }

    public void setDialogDescription(String contentDescription) {
        if (StringUtils.isTrimEmpty(contentDescription)) {
            description.setVisibility(View.GONE);
        }
        description.setText(contentDescription);
    }

    public void setDialogMsg(String contentMsg) {
        if (StringUtils.isTrimEmpty(contentMsg)) {
            msg.setVisibility(View.GONE);
        }
        msg.setText(contentMsg);
    }

    public void setDialogTitle(String contentTitle) {
        if (StringUtils.isTrimEmpty(contentTitle)) {
            title.setVisibility(View.GONE);
        }
        title.setText(contentTitle);
    }

    public void clickCancelBtn(OnClickListener onCancelClickListener) {
        btnCancel.setOnClickListener(
                v -> onCancelClickListener.onClick(LovelyAlertDialog.this, -2));
    }

    public void clickConfirmBtn(OnClickListener onConfirmClickListener) {
        btnConfirm.setOnClickListener(
                v -> onConfirmClickListener.onClick(LovelyAlertDialog.this, -1));
    }

    public void isShowCancelBtn(boolean showCancelBtn) {
        if (showCancelBtn) {
            btnCancel.setVisibility(View.VISIBLE);
        } else {
            btnCancel.setVisibility(View.GONE);
            btnConfirm.setWidth(-1);
        }
    }

    public void isShowConfirmBtn(boolean showConfirmBtn) {
        if (showConfirmBtn) {
            btnConfirm.setVisibility(View.VISIBLE);
        } else {
            btnConfirm.setVisibility(View.GONE);
            LinearLayout.LayoutParams params =
                    (LinearLayout.LayoutParams) btnCancel.getLayoutParams();
            params.weight = LinearLayout.LayoutParams.MATCH_PARENT;
            params.setMargins(SizeUtils.dp2px(30), 0, SizeUtils.dp2px(30), 0);
            btnCancel.setLayoutParams(params);
        }
    }

    public static class Builder {
        protected Context mContext;
        protected DialogParam param = new DialogParam();

        public Builder(Context context) {
            this.mContext = context;
        }

        public final Builder setTitle(String title) {
            param.contentTitle = title;
            return this;
        }

        public final Builder setMsg(String msg) {
            param.contentMsg = msg;
            return this;
        }

        public final Builder setDescription(String description) {
            param.contentDescription = description;
            return this;
        }

        public final Builder setTip(String tip) {
            param.contentTip = tip;
            return this;
        }

        public final Builder setConfirmBtnText(String confirmBtnText) {
            param.contentConfirmBtn = confirmBtnText;
            return this;
        }

        public final Builder setCancelBtnText(String cancelBtnText) {
            param.contentCancelBtn = cancelBtnText;
            return this;
        }

        public final Builder setColorTitle(int colorTitle) {
            param.colorTitle = colorTitle;
            return this;
        }

        public final Builder setColorMsg(int colorMsg) {
            param.colorMsg = colorMsg;
            return this;
        }

        public final Builder setColorDescription(int colorDescription) {
            param.colorDescription = colorDescription;
            return this;
        }

        public final Builder setColorTip(int colorTip) {
            param.colorTip = colorTip;
            return this;
        }

        public final Builder setColorConfirmBtn(int colorConfirmBtn) {
            param.colorConfirmBtn = colorConfirmBtn;
            return this;
        }

        public final Builder setColorCancelBtn(int colorCancelBtn) {
            param.colorCancelBtn = colorCancelBtn;
            return this;
        }

        public final Builder setIsCancelable(boolean isCancelable) {
            param.isCancelable = isCancelable;
            return this;
        }

        public final Builder setIsCanceledOnTouchOutside(boolean isCanceledOnTouchOutside) {
            param.isCanceledOnTouchOutside = isCanceledOnTouchOutside;
            return this;
        }

        public final Builder setShowConfirmBtn(boolean showConfirmBtn) {
            param.showConfirmBtn = showConfirmBtn;
            return this;
        }

        public final Builder setShowCancelBtn(boolean showCancelBtn) {
            param.showCancelBtn = showCancelBtn;
            return this;
        }

        public final Builder setBgTitle(String bgTitle) {
            param.bgTitle = bgTitle;
            return this;
        }

        public final Builder setonDismissListener(OnDismissListener onDismissListener) {
            param.onDismissListener = onDismissListener;
            return this;
        }

        public final Builder setOnCancelListener(OnCancelListener onCancelListener) {
            param.onCancelListener = onCancelListener;
            return this;
        }

        public final Builder setOnConfirmClickListener(OnClickListener onConfirmClickListener) {
            param.onConfirmClickListener = onConfirmClickListener;
            return this;
        }

        public final Builder setOnCancelClickListener(OnClickListener onCancelClickListener) {
            param.onCancelClickListener = onCancelClickListener;
            return this;
        }

        public final Builder setOnKeyListener(OnKeyListener onKeyListener) {
            param.onKeyListener = onKeyListener;
            return this;
        }

        public LovelyAlertDialog create() {
            LovelyAlertDialog dialog = new LovelyAlertDialog(this.mContext);
            dialog.createDialog(this.param);
            return dialog;
        }

        public final void show() {
            create().show();
        }
    }
}
