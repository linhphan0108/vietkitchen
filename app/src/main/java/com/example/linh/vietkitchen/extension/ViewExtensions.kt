package com.example.linh.vietkitchen.extension

import android.content.Context
import android.support.annotation.DimenRes
import android.view.View
import android.widget.TextView

val View.ctx: Context
    get() = context

fun View.getDimensionPixelSize(@DimenRes dimenRes: Int) = ctx.getDimensionPixelSize(dimenRes)
fun View.getDimension(@DimenRes dimenRes: Int) = ctx.getDimension(dimenRes)

var TextView.textColor: Int
    get() = currentTextColor
    set(v) = setTextColor(v)

fun View.slideExit() {
    if (translationY == 0f) animate().translationY(-height.toFloat())
}

fun View.slideEnter() {
    if (translationY < 0f) animate().translationY(0f)
}

fun View.lookTemporary(delay: Long = 200){
    this.isEnabled = false
    this.postDelayed({
        this.isEnabled = true
    },delay)
}