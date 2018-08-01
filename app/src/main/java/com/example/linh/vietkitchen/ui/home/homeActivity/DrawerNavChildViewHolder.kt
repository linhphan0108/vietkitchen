package com.example.linh.vietkitchen.ui.home.homeActivity

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import kotlinx.android.synthetic.main.item_child_drawer_nav.view.*

class DrawerNavChildViewHolder(itemView: View, private val listener: OnItemClickListener? = null) : RecyclerView.ViewHolder(itemView) {
    fun bindView(item: DrawerNavChildItem, payloads: MutableList<Any>) {
        itemView.txtListItem.text = item.itemTitle
        itemView.setOnClickListener{
            listener?.onItemClick(itemView, layoutPosition, adapterPosition, item)
        }
    }
}

interface OnItemClickListener{
    fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem? = null)
}