package com.example.aidltest.CustomView;

import android.content.Context;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.blankj.utilcode.util.StringUtils;

public class SearchEdit extends AppCompatEditText {
    private static final long LIMIT = 1000;

    private OnTextChangedListener mListener;
    private String mStartText = ""; // 记录开始输入前的文本内容

    public SearchEdit(Context context) {
        super(context);
    }

    public SearchEdit(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SearchEdit(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /** 在 LIMIT 时间内连续输入不触发文本变化 */
    public void setOnTextChangedListener(OnTextChangedListener listener) {
        mListener = listener;
    }

    private Runnable changeTextRunnable =
            new Runnable() {
                @Override
                public void run() {
                    if (mListener != null) {
                        if (!StringUtils.equals(mStartText, getText().toString())) {
                            mStartText = getText().toString(); // 更新 mStartText
                            mListener.onTextChanged(mStartText);
                        }
                    }
                }
            };

    @Override
    protected void onTextChanged(
            final CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        //        mDelayCount++;
        removeCallbacks(changeTextRunnable);
        postDelayed(changeTextRunnable, LIMIT);
    }

    public interface OnTextChangedListener {
        void onTextChanged(String text);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        removeCallbacks(changeTextRunnable);
    }
}
