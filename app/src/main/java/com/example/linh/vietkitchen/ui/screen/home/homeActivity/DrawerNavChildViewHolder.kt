package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.lookTemporary
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import kotlinx.android.synthetic.main.item_child_drawer_nav.view.*

class DrawerNavChildViewHolder(itemView: View, private val listener: OnItemClickListener? = null) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    fun bindView(item: DrawerNavChildItem, payloads: MutableList<Any>) {
        if (payloads.isNotEmpty()){
            payloads.forEach { p ->
                when(p as PayLoads){
                    PayLoads.SELECTED_CHANGED ->{
                        itemView.isSelected = item.isSelected
                    }
                }
            }
        }else{
            val context = itemView.context
            itemView.isSelected = item.isSelected
            itemView.txtListItem.text = context.getString(R.string.drawer_nav_item_title, item.itemTitle, item.numberItems)
            itemView.setOnClickListener{
                it.lookTemporary()
                listener?.onItemClick(itemView, layoutPosition, adapterPosition, item)
            }
        }
    }
}

interface OnItemClickListener{
    fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem? = null)
}