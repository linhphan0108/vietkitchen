package com.example.linh.vietkitchen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.LoadMoreItem
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class LoadMoreAdapterDelegate :  AbsListItemAdapterDelegate<LoadMoreItem, Entity, LoadMoreViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): LoadMoreViewHolder {
        val itemView = LayoutInflater.from(parent.ctx).inflate(R.layout.item_loadmore, parent, false)
        return LoadMoreViewHolder(itemView)
    }

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int): Boolean {
        return item is LoadMoreItem
    }

    override fun onBindViewHolder(item: LoadMoreItem, viewHolder: LoadMoreViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView()
    }
}