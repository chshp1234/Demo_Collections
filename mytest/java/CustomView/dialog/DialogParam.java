package com.example.aidltest.CustomView.dialog;

import android.content.DialogInterface;

public class DialogParam {
    String contentTitle;
    String contentMsg;
    String contentDescription;
    String contentTip;
    String contentConfirmBtn;
    String contentCancelBtn;

    int colorTitle = -1;
    int colorMsg = -1;
    int colorDescription = -1;
    int colorTip = -1;
    int colorConfirmBtn = -1;
    int colorCancelBtn = -1;

    boolean isCancelable = true;
    boolean isCanceledOnTouchOutside = false;
    boolean showConfirmBtn = true;
    boolean showCancelBtn = true;

    DialogInterface.OnDismissListener onDismissListener;
    DialogInterface.OnCancelListener onCancelListener;
    DialogInterface.OnClickListener onConfirmClickListener;
    DialogInterface.OnClickListener onCancelClickListener;
    DialogInterface.OnKeyListener onKeyListener;
    String bgTitle;

    public DialogParam() {}
}
