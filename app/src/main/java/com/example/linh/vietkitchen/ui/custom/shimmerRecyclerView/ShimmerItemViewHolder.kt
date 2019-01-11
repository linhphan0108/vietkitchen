package com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView

import androidx.recyclerview.widget.RecyclerView
import android.view.View

class ShimmerItemViewHolder(itemView: View
//                            private val shimmerItemView: ShimmerFrameLayout
//                                = itemView as ShimmerFrameLayout
                            ) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {


    fun startShimmerAnimation(){
//        shimmerItemView.repeatMode = ValueAnimator.RESTART
//        shimmerItemView.repeatCount = ValueAnimator.INFINITE
//        shimmerItemView.duration = 800
////        shimmerItemView.isAutoStart = true
//        shimmerItemView.startShimmerAnimation()
    }

    fun onViewAttachedToWindow(){
//        shimmerItemView.startShimmerAnimation()
    }

}