package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate

class DrawerNavChildItemDelegate(private val listener: OnItemClickListener? = null) : AbsListItemAdapterDelegate<DrawerNavChildItem, Entity, DrawerNavChildViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup)
            = DrawerNavChildViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_child_drawer_nav, parent, false), listener)

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int)
            = item is DrawerNavChildItem

    override fun onBindViewHolder(item: DrawerNavChildItem, viewHolder: DrawerNavChildViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item, payloads)
    }
}