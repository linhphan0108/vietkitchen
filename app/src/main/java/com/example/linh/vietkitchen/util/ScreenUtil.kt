package com.example.linh.vietkitchen.util

import android.content.Context
import android.content.res.Resources
import android.graphics.Rect
import android.util.Log
import android.view.View
import android.view.Window
import android.view.Window.ID_ANDROID_CONTENT
import timber.log.Timber


object ScreenUtil {
    /**
     * Android supports different screens, for example you might cast the app with Chromecast
     * or connect to a different screen by other means.
     * In that case the values will not be converted properly to that other screen.
     */
    fun dp2px(dp: Int): Int {
        val density = Resources.getSystem().displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    fun dp2px(context: Context, dp: Int): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }

    fun screenWidth() = Resources.getSystem().displayMetrics.widthPixels
    fun screenHeight() = Resources.getSystem().displayMetrics.heightPixels

    fun getMaxWidthImage(): Int{
        val screenWidth = screenWidth()
        return Math.min(screenWidth, ImageOptimizationUtil.MAX_IMAGE_WIDTH)
    }

    fun getStatusBarHeight(window: Window): Int {
        val rectangle = Rect()
        window.decorView.getWindowVisibleDisplayFrame(rectangle)
        val statusBarHeight = rectangle.top
        val contentViewTop = window.findViewById<View>(Window.ID_ANDROID_CONTENT).top
        val titleBarHeight = contentViewTop - statusBarHeight
        Timber.i("*** Elenasys :: StatusBar Height= $statusBarHeight , TitleBar Height = $titleBarHeight")
        return statusBarHeight
    }
}