package com.example.linh.vietkitchen.ui

import android.app.Application
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.util.NotLoggingTree
import timber.log.Timber

class VietKitchenApp : Application(){
    override fun onCreate() {
        super.onCreate()

        setupTimberLogger()
    }

    private fun setupTimberLogger(){
        if (BuildConfig.DEBUG){
            Timber.plant(object: Timber.DebugTree() {
                override fun createStackElementTag(element: StackTraceElement): String? {
                    return String.format("C:%s:%s",
                            super.createStackElementTag(element),
                            element.lineNumber)
                }
            })
        }else{
            Timber.plant(NotLoggingTree())
        }
    }
}