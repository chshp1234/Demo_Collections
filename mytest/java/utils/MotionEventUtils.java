package com.example.aidltest.utils;

import android.view.MotionEvent;

public class MotionEventUtils {
    public static String toFlage(MotionEvent event) {
        String flag = "";
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                flag = "ACTION_DOWN";
                break;
            case MotionEvent.ACTION_UP:
                flag = "ACTION_UP";
                break;
            case MotionEvent.ACTION_MOVE:
                flag = "ACTION_MOVE";
                break;
            case MotionEvent.ACTION_CANCEL:
                flag = "ACTION_CANCEL";
                break;
            default:
                flag = event.getAction() + "";
        }
        return flag;
    }
}
