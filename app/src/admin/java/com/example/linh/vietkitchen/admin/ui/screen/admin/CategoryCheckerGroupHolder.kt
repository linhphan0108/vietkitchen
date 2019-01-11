package com.example.linh.vietkitchen.admin.ui.screen.admin

import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.TextView
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem

class CategoryCheckerGroupHolder(itemView: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemView) {
    fun bindView(item: DrawerNavGroupItem, payloads: MutableList<Any>) {
        (itemView as TextView).text = item.headerTile
    }
}