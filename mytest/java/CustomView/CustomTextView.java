package com.example.aidltest.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Choreographer;
import android.view.MotionEvent;

import androidx.appcompat.widget.AppCompatTextView;

import com.blankj.utilcode.util.LogUtils;
import com.example.aidltest.utils.MotionEventUtils;

public class CustomTextView extends AppCompatTextView {

    public CustomTextView(Context context) {
        super(context);
    }

    public CustomTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtils.d("CustomTextView dispatchTouchEvent " + MotionEventUtils.toFlage(ev) + " ");
        boolean result = super.dispatchTouchEvent(ev);
        LogUtils.d(
                "CustomTextView dispatchTouchEvent "
                        + MotionEventUtils.toFlage(ev)
                        + " return "
                        + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d("CustomTextView onTouchEvent,action " + MotionEventUtils.toFlage(event) + " ");
        boolean result = super.onTouchEvent(event);
        LogUtils.d(
                "CustomTextView onTouchEvent,"
                        + MotionEventUtils.toFlage(event)
                        + " return "
                        + result
                        + " ");
        return result;
    }
}
