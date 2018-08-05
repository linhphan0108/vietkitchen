package com.example.linh.vietkitchen.ui

import android.support.multidex.MultiDexApplication
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.util.NotLoggingTree
import com.squareup.leakcanary.LeakCanary
import timber.log.Timber

class VietKitchenApp : MultiDexApplication(){
    override fun onCreate() {
        super.onCreate()

        if(setupLeaksCanary()) return

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

    /**
     * setup leak canary library
     * @return true if the lib is analysing heap dump, so the app should not be initialized
     * otherwise return false everything can go to normal
     */
    private fun setupLeaksCanary(): Boolean{
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return true
        }
        LeakCanary.install(this)
        // Normal app init code...
        return false
    }
}