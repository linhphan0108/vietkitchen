package com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView

import android.animation.ValueAnimator
import android.support.v7.widget.RecyclerView
import android.view.View
import com.facebook.shimmer.ShimmerFrameLayout

class ShimmerItemViewHolder(itemView: View,
                            private val shimmerItemView: ShimmerFrameLayout
                                = itemView as ShimmerFrameLayout
                            ) : RecyclerView.ViewHolder(itemView) {


    fun startShimmerAnimation(){
        shimmerItemView.repeatMode = ValueAnimator.RESTART
        shimmerItemView.repeatCount = ValueAnimator.INFINITE
        shimmerItemView.duration = 800
//        shimmerItemView.isAutoStart = true
        shimmerItemView.startShimmerAnimation()
    }

    fun onViewAttachedToWindow(){
        shimmerItemView.startShimmerAnimation()
    }

}