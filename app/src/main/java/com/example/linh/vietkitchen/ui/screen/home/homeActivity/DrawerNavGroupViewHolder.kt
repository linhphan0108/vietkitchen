package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.support.v4.content.ContextCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import kotlinx.android.synthetic.main.item_header_drawer_nav.view.*
import android.animation.ObjectAnimator
import android.graphics.drawable.RotateDrawable
import com.example.linh.vietkitchen.extension.getDimension


class DrawerNavGroupViewHolder(itemView: View, private val listener: OnGroupItemClickListener? = null)
    : RecyclerView.ViewHolder(itemView) {

    fun bindView(item: DrawerNavGroupItem, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()){
            payloads.forEach { p ->
                when(p as PayLoads){
                    PayLoads.SELECTED_CHANGED ->{
                        itemView.isSelected = item.isSelected
                    }
                }
            }
        }else {
            var expandAnimator: ObjectAnimator? = null
            val context = itemView.context
            itemView.txtListHeader.text = context.getString(R.string.drawer_nav_item_title, item.headerTile, item.numberItems)
            itemView.setOnClickListener {
                listener?.onGroupItemClick(itemView, layoutPosition, adapterPosition)
                if (item.isChildrenVisible){
                    expandAnimator?.start()
                }else{
                    expandAnimator?.reverse()
                }
            }
            if (item.itemsList.isNullOrEmpty()){
                itemView.txtListHeader.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)

            }else{
                val startDrawable = ContextCompat.getDrawable(context, R.drawable.ic_outline_expand_more_rotate) as RotateDrawable
                if (item.isChildrenVisible){
                    startDrawable.level = 10000
                }
                expandAnimator = ObjectAnimator.ofInt(startDrawable, "level", 0, 10000).setDuration(500)
                itemView.txtListHeader.setCompoundDrawablesWithIntrinsicBounds(startDrawable, null, null, null)
            }

            //since some empty group won't have compound drawable
            //so that reset the padding
            val topPadding = itemView.txtListHeader.paddingTop
            val endPadding = itemView.txtListHeader.paddingEnd
            val bottomPadding = itemView.txtListHeader.paddingBottom
            val startPadding = if (item.itemsList.isNullOrEmpty()) {
                context.getDimension(R.dimen.drawer_nav_group_title_drawable_padding_no_child).toInt()
            }else{
                context.getDimension(R.dimen.drawer_nav_group_title_drawable_padding).toInt()
            }
            itemView.txtListHeader.setPadding(startPadding, topPadding, endPadding, bottomPadding)
        }
    }
}

interface OnGroupItemClickListener{
    fun onGroupItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int)
}