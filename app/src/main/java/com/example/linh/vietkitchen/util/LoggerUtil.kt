package com.example.linh.vietkitchen.util

import android.os.Build
import android.os.Looper
import timber.log.Timber

class LoggerUtil {
    companion object {
        fun logThread(){
            Timber.d("current thread id = ${Thread.currentThread().id} name = ${Thread.currentThread().name}")
            Timber.d("main thread id = ${Looper.getMainLooper().thread.id} name = ${Looper.getMainLooper().thread.name}")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Timber.d("is running on main thread = ${Looper.getMainLooper().isCurrentThread}")
            }
        }
    }
}