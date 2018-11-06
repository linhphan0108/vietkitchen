package com.example.linh.vietkitchen.util

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View





class VerticalSpaceItemDecoration(private val verticalSpace: Int = 0,
                                  private val firstItem: Int = 0,
                                  private val lastItem: Int = 0)
    : RecyclerView.ItemDecoration() {

//    private val divider: Drawable? = null

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView,
                                state: RecyclerView.State) {
        outRect.bottom = verticalSpace
        val position = parent.getChildAdapterPosition(view)
        if (position == 0){
            outRect.top = firstItem
        }else if (position == parent.adapter!!.itemCount - 1){
            outRect.bottom = lastItem
        }
    }

    /**
     * Custom divider will be used
     */
//    fun DividerItemDecoration(context: Context, resId: Int): ??? {
//        divider = ContextCompat.getDrawable(context, resId)
//    }
//
//    fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
//        val left = parent.paddingLeft
//        val right = parent.width - parent.paddingRight
//
//        val childCount = parent.childCount
//        for (i in 0 until childCount) {
//            val child = parent.getChildAt(i)
//
//            val params = child.layoutParams as RecyclerView.LayoutParams
//
//            val top = child.bottom + params.bottomMargin
//            val bottom = top + divider.getIntrinsicHeight()
//
//            divider.setBounds(left, top, right, bottom)
//            divider.draw(c)
//        }
//    }
}