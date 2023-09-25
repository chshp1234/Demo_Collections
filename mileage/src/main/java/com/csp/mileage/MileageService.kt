package com.csp.mileage

import android.app.Service
import android.content.Intent
import android.os.IBinder

/**
 * created by dongdaqing 2022/3/7 2:16 下午
 */
class MileageService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}