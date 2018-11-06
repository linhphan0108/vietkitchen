package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate

class CategoryCheckChildDelegate (private val listener: CategoryCheckerChildHolder.OnItemClickListener)
: AbsListItemAdapterDelegate<DrawerNavChildItem, Entity, CategoryCheckerChildHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup)
            = CategoryCheckerChildHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_category_checker_child, parent, false), listener)

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int)
            = item is DrawerNavChildItem

    override fun onBindViewHolder(item: DrawerNavChildItem, viewHolder: CategoryCheckerChildHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item, payloads)
    }
}