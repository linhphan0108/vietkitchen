package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavHeader
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class DrawerNavHeaderLayoutDelegate : AbsListItemAdapterDelegate<DrawerNavHeader, Entity, DrawerNavHeaderViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup): DrawerNavHeaderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_home_nav_header, parent, false)
        return DrawerNavHeaderViewHolder(itemView)
    }

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int) = item is DrawerNavHeader

    override fun onBindViewHolder(item: DrawerNavHeader, viewHolder: DrawerNavHeaderViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView()
    }

}