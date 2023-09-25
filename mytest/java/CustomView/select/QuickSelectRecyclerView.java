package com.example.aidltest.CustomView.select;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class QuickSelectRecyclerView extends RecyclerView {

    public static final int STATE_SELECT = 1;
    public static final int STATE_UNSELECT = 2;

    private boolean state;

    private int selectState;

    public QuickSelectRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public QuickSelectRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickSelectRecyclerView(
            @NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private boolean isSelect(int index) {
        return getChildAt(index).isSelected();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getAdapter() == null
                || !(getAdapter() instanceof GroupChatSelectAdapter)
                || getAdapter().getItemCount() == 0) {
            return super.dispatchTouchEvent(ev);
        }
        if (state) {
            // 获取触摸时对应的条目位置下标
            final View v = findChildViewUnder(ev.getX(), ev.getY());

            if (v == null) {
                return super.dispatchTouchEvent(ev);
            }

            switch (ev.getAction()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_CANCEL:
                    state = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    updateSelectState(getChildAdapterPosition(v));
                    break;
            }

            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void updateSelectState(int position) {
        if (position == NO_POSITION) {
            return;
        }

        switch (selectState) {
            case STATE_SELECT:
                if (!((GroupChatSelectAdapter) getAdapter()).isSelect(position)) {
                    ((GroupChatSelectAdapter) getAdapter()).setSelect(position, true);
                }
                break;
            case STATE_UNSELECT:
                if (((GroupChatSelectAdapter) getAdapter()).isSelect(position)) {
                    ((GroupChatSelectAdapter) getAdapter()).setSelect(position, false);
                }
                break;
        }
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }

    public int getSelectState() {
        return selectState;
    }

    public void setSelectState(int selectState) {
        this.selectState = selectState;
    }
}
