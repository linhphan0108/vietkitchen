package com.example.linh.vietkitchen.admin.ui.screen.admin

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import kotlinx.android.synthetic.admin.item_category_checker_child.view.*

class CategoryCheckerChildHolder(itemView: View, private val listener: OnItemClickListener)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    fun bindView(item: DrawerNavChildItem, payloads: MutableList<Any>) {
        itemView.cTxt.text = item.itemTitle
        itemView.setOnClickListener{
            val isChecked = !itemView.cTxt.isChecked
            listener.onItemClick(itemView, layoutPosition, adapterPosition, item, isChecked)
            itemView.cTxt.isChecked = isChecked
            itemView.cTxt.setCheckMarkDrawable(
                    if(isChecked) android.R.drawable.checkbox_on_background
                        else android.R.drawable.checkbox_off_background)
        }
    }

    interface OnItemClickListener{
        fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: DrawerNavChildItem, checked: Boolean)
    }
}