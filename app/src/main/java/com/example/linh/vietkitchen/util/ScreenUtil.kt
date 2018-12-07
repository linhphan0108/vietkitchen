package com.example.linh.vietkitchen.util

import android.content.Context
import android.content.res.Resources

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
}