package com.example.linh.vietkitchen.ui.home.homeActivity

import android.graphics.Typeface
import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import kotlinx.android.synthetic.main.item_header_drawer_nav.view.*

class DrawerNavGroupViewHolder(itemView: View, private val listener: OnItemClickListener? = null)
    : RecyclerView.ViewHolder(itemView) {

    fun bindView(item: DrawerNavGroupItem, payloads: MutableList<Any>) {
        itemView.txtListHeader.text = item.headerTile
        itemView.setOnClickListener{
            listener?.onGroupItemClick(itemView, layoutPosition, adapterPosition)
        }
    }

    interface OnItemClickListener{
        fun onGroupItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int)
    }
}