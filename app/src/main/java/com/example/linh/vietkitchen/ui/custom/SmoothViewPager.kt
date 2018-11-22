package com.example.linh.vietkitchen.ui.custom

import android.content.Context
import android.support.v4.view.ViewPager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.View
import com.example.linh.vietkitchen.extension.ctx
import timber.log.Timber


class SmoothViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0) : ViewPager(context, attrs) {

    init {
        changePagerScroller()
    }

    private fun changePagerScroller() {
        try {
            val mScroller = ViewPager::class.java.getDeclaredField("mScroller")
//            val interpolator = ViewPager::class.java.getDeclaredField("sInterpolator")
            mScroller.isAccessible = true
            val scroller = SmoothViewPagerScroller(ctx)
            mScroller.set(this, scroller)
        } catch (e: Exception) {
            Timber.e(e, "error of change scroller")
        }

    }

    override fun canScroll(v: View?, checkV: Boolean, dx: Int, x: Int, y: Int): Boolean {
        if (v is RecyclerView) {
            return true
        }
        return super.canScroll(v, checkV, dx, x, y)
    }


}