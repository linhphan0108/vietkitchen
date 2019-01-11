package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class CategoryCheckerGroupDelegate : AbsListItemAdapterDelegate<DrawerNavGroupItem,
        Entity, CategoryCheckerGroupHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup): CategoryCheckerGroupHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_category_checker_group, parent, false)
        return CategoryCheckerGroupHolder(itemView)
    }

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int)
            = item is DrawerNavGroupItem

    override fun onBindViewHolder(item: DrawerNavGroupItem, viewHolder: CategoryCheckerGroupHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item, payloads)
    }
}