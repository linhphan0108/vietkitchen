package com.example.linh.vietkitchen.extension

import android.os.Looper
import android.os.NetworkOnMainThreadException
import com.example.linh.vietkitchen.BuildConfig
import timber.log.Timber

object TimberUtils{
    fun checkThread(tag: String) {
        Timber.d("$tag is running on thread: ${Thread.currentThread()}")
        Timber.d("$tag is running MainThread : ${Looper.myLooper() == Looper.getMainLooper()}")
        Timber.d("$tag is running MainLooper : ${Looper.getMainLooper().thread == Thread.currentThread()}")
    }

    fun checkNotMainThread(){
        if (BuildConfig.DEBUG && Looper.getMainLooper().thread == Thread.currentThread()){
            throw NetworkOnMainThreadException()
        }
    }
}