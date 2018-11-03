package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import kotlinx.android.synthetic.main.item_header_drawer_nav.view.*

class DrawerNavGroupViewHolder(itemView: View, private val listener: OnGroupItemClickListener? = null)
    : RecyclerView.ViewHolder(itemView) {

    fun bindView(item: DrawerNavGroupItem, payloads: MutableList<Any>) {
        val context = itemView.context
        itemView.txtListHeader.text = context.getString(R.string.drawer_nav_item_title, item.headerTile, item.numberItems)
        itemView.setOnClickListener{
            listener?.onGroupItemClick(itemView, layoutPosition, adapterPosition)
        }
    }
}

interface OnGroupItemClickListener{
    fun onGroupItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int)
}