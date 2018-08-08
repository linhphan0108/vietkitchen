package com.example.linh.vietkitchen.util

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class VerticalSpaceItemDecoration(private val verticalSpaceTop: Int = 0,
                                  private val verticalSpace: Int = 0) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        if (parent.getChildAdapterPosition(view) == 0){
            outRect.top = verticalSpaceTop
        }
        outRect.bottom = verticalSpace
    }
}