package com.example.linh.vietkitchen.ui.custom

import android.content.Context
import androidx.viewpager.widget.ViewPager
import android.util.AttributeSet
import com.example.linh.vietkitchen.extension.ctx
import timber.log.Timber


class SmoothViewPager @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null,
                                                defStyleAttr: Int = 0) : androidx.viewpager.widget.ViewPager(context, attrs) {

    init {
        changePagerScroller()
    }

    private fun changePagerScroller() {
        try {
            val mScroller = androidx.viewpager.widget.ViewPager::class.java.getDeclaredField("mScroller")
//            val interpolator = ViewPager::class.java.getDeclaredField("sInterpolator")
            mScroller.isAccessible = true
            val scroller = SmoothViewPagerScroller(ctx)
            mScroller.set(this, scroller)
        } catch (e: Exception) {
            Timber.e(e, "error of change scroller")
        }

    }
}