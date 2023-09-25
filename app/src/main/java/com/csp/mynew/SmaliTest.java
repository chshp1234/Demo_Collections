package com.csp.mynew;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.csp.nativelibtest.NativeLib;
import com.csp.nativelibtest.NativeLibKt;

import java.util.Set;

public class SmaliTest {
    private static final String TAG = "MySmali";

    public void log() {
        String sb = "";

    }

    public static void printStack() {
        NativeLib.INSTANCE.stringFromJNI();
        //If you want to print stack trace on console than use dumpStack() method
//        System.err.println("Stack trace of current thread using dumpStack() method");
//        Thread.currentThread().dumpStack();
        //This is another way to print stack trace from current method System.err.println("Printing stack trace using printStackTrace() method of Throwable ");
//        new Throwable().printStackTrace();
        //If you want stack trace as StackTraceElement in program itself than
        //use getStackTrace() method of Thread class
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        //Once you get StackTraceElement you can also print it to console
//        System.err.println("displaying Stack trace from StackTraceElement in Java");
        if (stackTrace == null) {
            return;
        }
        int index = 0;
        while (index < stackTrace.length) {
            if ("printStack".equals(stackTrace[index++].getMethodName())) {
                break;
            }
        }
        LogE(" ");
        LogE("====================开始打印调用栈====================");
        while (index < stackTrace.length) {
            LogE(stackTrace[index].getClassName() + ": " + stackTrace[index].getMethodName() + "()");
            index++;
        }
        LogE("====================结束打印调用栈====================");
        LogE(" ");
    }

    public static void getIntentKV(Intent intent) {
        if (intent == null) {
            LogE("intent is null");
            return;
        }
        try {

            LogE("====================开始打印Intent====================");
            LogE(intent.toString());
            Bundle extras = intent.getExtras();
            if (extras != null) {
                Set<String> keys = extras.keySet();
                if (keys != null) {
                    for (String key : keys) {
                        LogE("key=" + key + "\t\t" + "value=" + stringOf(extras.get(key)));
                    }
                }
            }
        } catch (Exception e) {
            LogE(e.getMessage());
        } finally {
            LogE("====================结束打印Intent====================");
        }
    }

    private static void LogE(String msg) {
        if (msg == null) {
            Log.e(TAG, "null");
            return;
        }
        Log.e(TAG, msg);
    }

    private static String stringOf(Object o) {
        return String.valueOf(o);
    }
}
