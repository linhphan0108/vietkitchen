package com.example.linh.vietkitchen.ui.custom

import android.content.Context
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View


class InterceptedDrawerLayout
@JvmOverloads
constructor(context: Context, private var attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : DrawerLayout(context, attrs, defStyleAttr) {

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    constructor(
//            context: Context,
//            attrs: AttributeSet?,
//            defStyleAttr: Int,
//            defStyleRes: Int)
//            : super(context, attrs, defStyleAttr, defStyleRes){
//        this.attrs = attrs
//    }

    var shouldInterceptTouchEvent = false
    private var mNavigationView: View? = null
        get() {
            if (field != null) return field

            if (childCount > 0){
                for (i in 0..childCount){
                    val child = getChildAt(i)
                    if (child is NavigationView){
                        return child
                    }
                }
            }
            return null
        }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (mNavigationView != null){
            return if (shouldInterceptTouchEvent && isDrawerOpen(mNavigationView!!))
                false else super.onInterceptTouchEvent(ev)
        }

        return super.onInterceptTouchEvent(ev)
    }
}