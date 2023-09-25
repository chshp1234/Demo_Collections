package com.example.aidltest.CustomView;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.SizeUtils;
import com.blankj.utilcode.util.SpanUtils;
import com.example.aidltest.R;
import com.example.aidltest.utils.ClickUtils;
import com.example.aidltest.utils.MotionEventUtils;

public class GoodsSortView extends LinearLayout
        implements View.OnTouchListener, View.OnClickListener {
    public static final int STATE_UNITE = 1;
    public static final int STATE_SALES_VOLUME_UP = 2;
    public static final int STATE_SALES_VOLUME_DOWN = 3;
    public static final int STATE_PRICE_UP = 4;
    public static final int STATE_PRICE_DOWN = 5;

    private int currentState = 1;

    private onItemClickListener onItemClickListener;

    private View rootView;
    private Context mContext;
    private LayoutInflater inflater;
    private TextView btnSortUnite;
    private TextView btnSortSalesVolume;
    private TextView btnSortPrice;

    public GoodsSortView(Context context) {
        this(context, null);
    }

    public GoodsSortView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GoodsSortView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        inflater = LayoutInflater.from(context);

        initView();
    }

    private void initView() {

        rootView = LayoutInflater.from(mContext).inflate(R.layout.view_goods_sort, this);

        btnSortUnite = rootView.findViewById(R.id.sort_unite);
        btnSortSalesVolume = rootView.findViewById(R.id.sort_sales_volume);
        btnSortPrice = rootView.findViewById(R.id.sort_price);

        SpanUtils.with(btnSortSalesVolume)
                .append("销量")
                .appendSpace(SizeUtils.dp2px(6))
                .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                .create();
        SpanUtils.with(btnSortPrice)
                .append("价格")
                .appendSpace(SizeUtils.dp2px(6))
                .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                .create();

        rootView.setOnTouchListener(this);
        //                        btnSortUnite.setOnClickListener(this);
        btnSortUnite.setOnTouchListener(this);
        //                        btnSortSalesVolume.setOnClickListener(this);
        btnSortSalesVolume.setOnTouchListener(this);
        //                        btnSortPrice.setOnClickListener(this);
        btnSortPrice.setOnTouchListener(this);

        //        btnSortPrice.getParent().requestDisallowInterceptTouchEvent(true);

        //        addView(rootView, btnSortUnite.getLayoutParams());
    }

    /*todo onClick触发不了？用onTouch代替解决*/
    @Override
    public void onClick(View v) {
        LogUtils.d("view=" + v.getTag() + " onClick");
        switch (v.getId()) {
            case R.id.sort_unite:
                if (currentState != STATE_UNITE) {
                    currentState = STATE_UNITE;

                    btnSortUnite.setTextColor(mContext.getColor(R.color.red));
                    btnSortSalesVolume.setTextColor(mContext.getColor(R.color.text_black));
                    btnSortPrice.setTextColor(mContext.getColor(R.color.text_black));

                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                            .create();
                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                            .create();
                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(currentState);
                    }
                }
                break;
            case R.id.sort_sales_volume:
                if (currentState == STATE_SALES_VOLUME_UP) {
                    currentState = STATE_SALES_VOLUME_DOWN;
                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                            .create();
                } else if (currentState == STATE_SALES_VOLUME_DOWN) {
                    currentState = STATE_SALES_VOLUME_UP;
                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                            .create();
                } else {
                    currentState = STATE_SALES_VOLUME_DOWN;
                    btnSortSalesVolume.setTextColor(mContext.getColor(R.color.red));
                    btnSortUnite.setTextColor(mContext.getColor(R.color.text_black));
                    btnSortPrice.setTextColor(mContext.getColor(R.color.text_black));

                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                            .create();

                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                            .create();
                }

                if (onItemClickListener != null) {
                    onItemClickListener.onClick(currentState);
                }
                break;
            case R.id.sort_price:
                if (currentState == STATE_PRICE_UP) {
                    currentState = STATE_PRICE_DOWN;
                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                            .create();
                } else if (currentState == STATE_PRICE_DOWN) {
                    currentState = STATE_PRICE_UP;
                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                            .create();
                } else {
                    currentState = STATE_PRICE_UP;
                    btnSortPrice.setTextColor(mContext.getColor(R.color.red));

                    btnSortUnite.setTextColor(mContext.getColor(R.color.text_black));
                    btnSortSalesVolume.setTextColor(mContext.getColor(R.color.text_black));

                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                            .create();

                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                            .create();
                }

                if (onItemClickListener != null) {
                    onItemClickListener.onClick(currentState);
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        LogUtils.d("view=" + v.getTag() + " onTouch " + MotionEventUtils.toFlage(event) + " ");
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            return false;
        }
        boolean result = false;
        if (event.getAction() == MotionEvent.ACTION_UP && ClickUtils.isFastClick()) {
            switch (v.getId()) {
                case R.id.sort_unite:
                    if (currentState != STATE_UNITE) {
                        currentState = STATE_UNITE;

                        btnSortUnite.setTextColor(mContext.getColor(R.color.red));
                        btnSortSalesVolume.setTextColor(mContext.getColor(R.color.text_black));
                        btnSortPrice.setTextColor(mContext.getColor(R.color.text_black));

                        SpanUtils.with(btnSortSalesVolume)
                                .append("销量")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                                .create();
                        SpanUtils.with(btnSortPrice)
                                .append("价格")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                                .create();
                        if (onItemClickListener != null) {
                            onItemClickListener.onClick(currentState);
                        }
                    }
                    result = true;
                    break;
                case R.id.sort_sales_volume:
                    if (currentState == STATE_SALES_VOLUME_UP) {
                        currentState = STATE_SALES_VOLUME_DOWN;
                        SpanUtils.with(btnSortSalesVolume)
                                .append("销量")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                                .create();
                    } else if (currentState == STATE_SALES_VOLUME_DOWN) {
                        currentState = STATE_SALES_VOLUME_UP;
                        SpanUtils.with(btnSortSalesVolume)
                                .append("销量")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                                .create();
                    } else {
                        currentState = STATE_SALES_VOLUME_DOWN;
                        btnSortSalesVolume.setTextColor(mContext.getColor(R.color.red));
                        btnSortUnite.setTextColor(mContext.getColor(R.color.text_black));
                        btnSortPrice.setTextColor(mContext.getColor(R.color.text_black));

                        SpanUtils.with(btnSortSalesVolume)
                                .append("销量")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                                .create();

                        SpanUtils.with(btnSortPrice)
                                .append("价格")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                                .create();
                    }

                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(currentState);
                    }
                    result = true;
                    break;
                case R.id.sort_price:
                    if (currentState == STATE_PRICE_UP) {
                        currentState = STATE_PRICE_DOWN;
                        SpanUtils.with(btnSortPrice)
                                .append("价格")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                                .create();
                    } else if (currentState == STATE_PRICE_DOWN) {
                        currentState = STATE_PRICE_UP;
                        SpanUtils.with(btnSortPrice)
                                .append("价格")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                                .create();
                    } else {
                        currentState = STATE_PRICE_UP;
                        btnSortPrice.setTextColor(mContext.getColor(R.color.red));

                        btnSortUnite.setTextColor(mContext.getColor(R.color.text_black));
                        btnSortSalesVolume.setTextColor(mContext.getColor(R.color.text_black));

                        SpanUtils.with(btnSortPrice)
                                .append("价格")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                                .create();

                        SpanUtils.with(btnSortSalesVolume)
                                .append("销量")
                                .appendSpace(SizeUtils.dp2px(6))
                                .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                                .create();
                    }

                    if (onItemClickListener != null) {
                        onItemClickListener.onClick(currentState);
                    }
                    result = true;
                    break;
            }
        }
        LogUtils.d(
                "view="
                        + v.getTag()
                        + " onTouch "
                        + MotionEventUtils.toFlage(event)
                        + " return "
                        + result);
        return result;
    }

    public interface onItemClickListener {
        void onClick(int state);
    }

    public GoodsSortView.onItemClickListener getOnItemClickListener() {
        return onItemClickListener;
    }

    public void setOnItemClickListener(GoodsSortView.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getCurrentState() {
        return currentState;
    }

    public void setCurrentState(int currentState) {
        this.currentState = currentState;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        LogUtils.d("GoodsSortView onInterceptTouchEvent " + MotionEventUtils.toFlage(ev) + " ");
        boolean isIntercept = super.onInterceptTouchEvent(ev);
        LogUtils.d(
                "GoodsSortView onInterceptTouchEvent "
                        + MotionEventUtils.toFlage(ev)
                        + " return "
                        + isIntercept);
        return isIntercept;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        LogUtils.d("GoodsSortView dispatchTouchEvent " + MotionEventUtils.toFlage(ev) + " ");
        boolean result = super.dispatchTouchEvent(ev);
        LogUtils.d(
                "GoodsSortView dispatchTouchEvent "
                        + MotionEventUtils.toFlage(ev)
                        + " return "
                        + result);
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        LogUtils.d("GoodsSortView onTouchEvent,action " + MotionEventUtils.toFlage(event) + " ");
        boolean result = super.onTouchEvent(event);
        LogUtils.d(
                "GoodsSortView onTouchEvent,"
                        + MotionEventUtils.toFlage(event)
                        + " return "
                        + result
                        + " ");
        return result;
    }

    public void updateState(int state) {
        if (currentState != state) {
            switch (currentState) {
                case STATE_UNITE:
                    btnSortUnite.setTextColor(mContext.getColor(R.color.text_black));
                    break;
                case STATE_SALES_VOLUME_DOWN:
                case STATE_SALES_VOLUME_UP:
                    btnSortSalesVolume.setTextColor(mContext.getColor(R.color.text_black));
                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                            .create();
                    break;
                case STATE_PRICE_DOWN:
                case STATE_PRICE_UP:
                    btnSortPrice.setTextColor(mContext.getColor(R.color.text_black));
                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_gray, SpanUtils.ALIGN_CENTER)
                            .create();
                    break;
            }
            switch (state) {
                case STATE_UNITE:
                    btnSortUnite.setTextColor(mContext.getColor(R.color.red));
                    break;
                case STATE_SALES_VOLUME_DOWN:
                    btnSortSalesVolume.setTextColor(mContext.getColor(R.color.red));
                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                            .create();
                    break;
                case STATE_SALES_VOLUME_UP:
                    btnSortSalesVolume.setTextColor(mContext.getColor(R.color.red));
                    SpanUtils.with(btnSortSalesVolume)
                            .append("销量")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                            .create();
                    break;
                case STATE_PRICE_DOWN:
                    btnSortPrice.setTextColor(mContext.getColor(R.color.red));
                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_down, SpanUtils.ALIGN_CENTER)
                            .create();
                    break;
                case STATE_PRICE_UP:
                    btnSortPrice.setTextColor(mContext.getColor(R.color.red));
                    SpanUtils.with(btnSortPrice)
                            .append("价格")
                            .appendSpace(SizeUtils.dp2px(6))
                            .appendImage(R.drawable.arrow_up, SpanUtils.ALIGN_CENTER)
                            .create();
                    break;
            }
            currentState = state;
        }
    }
}
