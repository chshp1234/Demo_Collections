package com.csp.mynew;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileDescriptor;

public class MyGroup extends LinearLayout {

    public MyGroup(Context context) {
        super(context);
    }

    public MyGroup(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MyGroup(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MyGroup(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        /*boolean isSwipe = true;
        if (isSwipe) {
            return false;
        }*/


        return super.dispatchTouchEvent(ev);
    }
}
