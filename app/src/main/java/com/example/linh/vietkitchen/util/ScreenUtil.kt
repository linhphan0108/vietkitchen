package com.example.linh.vietkitchen.util

import android.content.Context

object ScreenUtil {
    fun dp2px(context: Context, dp: Float): Int {
        val scale = context.resources.displayMetrics.density
        return (dp * scale + 0.5f).toInt()
    }
}