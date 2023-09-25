package com.example.aidltest.CustomView.select;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.aidltest.R;
import com.google.android.material.checkbox.MaterialCheckBox;

public class SelectItem extends FrameLayout {

    private boolean isChecked;
    private View rootView;
    private MaterialCheckBox checkBox;

    public SelectItem(@NonNull Context context) {
        this(context, null);
    }

    public SelectItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        rootView = LayoutInflater.from(context).inflate(R.layout.item_group_chat_info_select, this);
        checkBox = rootView.findViewById(R.id.check);
        setLongClickable(false);
    }

    public void setChecked(boolean checked) {
        checkBox.setChecked(checked);
        isChecked = checked;
    }

    @Override
    public boolean isSelected() {
        return isChecked;
    }
}
