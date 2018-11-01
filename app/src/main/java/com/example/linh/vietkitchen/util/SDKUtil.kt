package com.example.linh.vietkitchen.util

import android.os.Build

object SDKUtil {
    fun atLeastKitKat() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
    fun atLeastLillopop() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP
}