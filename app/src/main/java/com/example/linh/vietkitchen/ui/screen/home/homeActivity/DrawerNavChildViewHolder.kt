package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import kotlinx.android.synthetic.main.item_child_drawer_nav.view.*

class DrawerNavChildViewHolder(itemView: View, private val listener: OnItemClickListener? = null) : RecyclerView.ViewHolder(itemView) {
    fun bindView(item: DrawerNavChildItem, payloads: MutableList<Any>) {
        val context = itemView.context
        itemView.txtListItem.text = context.getString(R.string.drawer_nav_item_title, item.itemTitle, item.numberItems)
        itemView.setOnClickListener{
            listener?.onItemClick(itemView, layoutPosition, adapterPosition, item)
        }
    }
}

interface OnItemClickListener{
    fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem? = null)
}