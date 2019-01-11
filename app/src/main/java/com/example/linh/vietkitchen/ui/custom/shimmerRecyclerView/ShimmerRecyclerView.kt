package com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.util.AttributeSet
import android.view.MotionEvent

class ShimmerRecyclerView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : androidx.recyclerview.widget.RecyclerView(context, attrs, defStyleAttr) {

    lateinit var shimmerAdapter: ShimmerAdapter

    override fun setAdapter(adapter: Adapter<*>?) {
        super.setAdapter(adapter)
        if(adapter is ShimmerAdapter){
            shimmerAdapter = adapter
        }else{
            throw ClassNotFoundException("the adapter must be derived from ShimmerAdapter")
        }
    }

//    override fun onInterceptTouchEvent(e: MotionEvent?): Boolean {
//        return if (shimmerAdapter.isShimmerAnimationReFresh){
//            true
//        }else {
//            super.onInterceptTouchEvent(e)
//        }
//    }
}