package com.example.aidltest.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatEditText;

import com.example.aidltest.R;

public class SearchEditText extends AppCompatEditText {
    private static final int DEFAULT_INTERVAL = 500;
    private static final boolean DEFAULT_REPETITION = true;

    private static final int WHAT_TEXT_CHANGE = 100;
    private static final int WHAT_TEXT_CONFIRM = 200;
    private static final int WHAT_TEXT_DEFAULT = 300;

    private int interval;
    private boolean repetition;

    private WatchEditChangeListener onWatchEditChangeListener;

    private Handler textChangeHandler;
    private HandlerThread textChangeHandlerThread;

    public WatchEditChangeListener getOnWatchEditChangeListener() {
        return onWatchEditChangeListener;
    }

    public void setOnWatchEditChangeListener(WatchEditChangeListener onWatchEditChangeListener) {
        this.onWatchEditChangeListener = onWatchEditChangeListener;
    }

    public SearchEditText(Context context) {
        this(context, null);
    }

    public SearchEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SearchEditText(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttribute(context, attrs);
        initListener();
    }

    private void initAttribute(Context context, AttributeSet attributeSet) {
        // 读取配置
        TypedArray array = context.obtainStyledAttributes(attributeSet, R.styleable.SearchEditText);
        interval = array.getInteger(R.styleable.SearchEditText_interval, DEFAULT_INTERVAL);
        if (interval < 0) {
            interval = DEFAULT_INTERVAL;
        }
        repetition = array.getBoolean(R.styleable.SearchEditText_repetition, DEFAULT_REPETITION);
        array.recycle();
    }

    private void initListener() {
        textChangeHandlerThread = new HandlerThread("textChangeHandlerThread");
        textChangeHandlerThread.start();
        textChangeHandler =
                new Handler(textChangeHandlerThread.getLooper()) {
                    @Override
                    public void handleMessage(Message msg) {
                        switch (msg.what) {
                            case WHAT_TEXT_CHANGE:
                                textChangeHandler.removeMessages(WHAT_TEXT_CONFIRM);

                                Message messagePoll =
                                        textChangeHandler.obtainMessage(WHAT_TEXT_CONFIRM, msg.obj);
                                textChangeHandler.sendMessageDelayed(messagePoll, interval);

                                break;
                            case WHAT_TEXT_CONFIRM:
                                if (msg.obj != null) {
                                    String search = msg.obj.toString();
                                    if (repetition
                                            || (getText() != null
                                                    && !search.equals(getText().toString()))) {
                                        if (onWatchEditChangeListener != null) {
                                            onWatchEditChangeListener.onEditChange(search);
                                        }
                                    }
                                    //
                                }
                                break;
                            case WHAT_TEXT_DEFAULT:
                                break;
                        }
                    }
                };
    }

    @Override
    protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter);
        Message message = textChangeHandler.obtainMessage(WHAT_TEXT_CHANGE, text.toString());
        textChangeHandler.sendMessage(message);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        textChangeHandler.removeMessages(WHAT_TEXT_CONFIRM);
    }

    public interface WatchEditChangeListener {
        void onEditChange(String content);
    }
}
