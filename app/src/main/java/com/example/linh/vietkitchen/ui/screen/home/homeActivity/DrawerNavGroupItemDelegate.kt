package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class DrawerNavGroupItemDelegate(private val listener: OnGroupItemClickListener? = null): AbsListItemAdapterDelegate<DrawerNavGroupItem,
        Entity, DrawerNavGroupViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup): DrawerNavGroupViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_group_drawer_nav, parent, false)
        return DrawerNavGroupViewHolder(itemView, listener)
    }

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int)
            = item is DrawerNavGroupItem

    override fun onBindViewHolder(item: DrawerNavGroupItem, viewHolder: DrawerNavGroupViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item, payloads)
    }
}