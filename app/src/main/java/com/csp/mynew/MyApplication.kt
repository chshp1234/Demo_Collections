package com.csp.mynew

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.blankj.utilcode.util.LogUtils

/**
 * created by dongdaqing 2021/9/9 4:12 下午
 */

const val TAG = "MyApplication"

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        application = this
        LogUtils.getConfig().stackDeep = 3
        ProcessLifecycleOwner.get().lifecycle.addObserver(MyApplicationObserver())
    }

    companion object {
        lateinit var application: Application
    }

    class MyApplicationObserver : DefaultLifecycleObserver {
        override fun onCreate(owner: LifecycleOwner) {
            LogUtils.d("application onCreate")
            super.onCreate(owner)
        }

        override fun onStart(owner: LifecycleOwner) {
            LogUtils.d("application onStart")
            super.onStart(owner)
        }

        override fun onResume(owner: LifecycleOwner) {
            LogUtils.d("application onResume")
            super.onResume(owner)
        }

        override fun onPause(owner: LifecycleOwner) {
            LogUtils.d("application onPause")
            super.onPause(owner)
        }

        override fun onStop(owner: LifecycleOwner) {
            LogUtils.d("application onStop")
            super.onStop(owner)
        }

        override fun onDestroy(owner: LifecycleOwner) {
            LogUtils.d("application onDestroy")
            super.onDestroy(owner)
        }
    }
}

object MyObserver {
    val TAG = "MyObserver"
}