package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.view.ViewGroup
import com.example.linh.vietkitchen.ui.model.DrawerNavHeader
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate

class DrawerNavHeaderLayoutDelegate(private val userInfo: UserInfo) : AbsListItemAdapterDelegate<DrawerNavHeader, Entity, DrawerNavHeaderViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup): DrawerNavHeaderViewHolder {
        return DrawerNavHeaderViewHolder(parent, userInfo)
    }

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int) = item is DrawerNavHeader

    override fun onBindViewHolder(item: DrawerNavHeader, viewHolder: DrawerNavHeaderViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView()
    }

}