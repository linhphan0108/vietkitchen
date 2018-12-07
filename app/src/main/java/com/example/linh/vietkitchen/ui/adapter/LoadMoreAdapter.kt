package com.example.linh.vietkitchen.ui.adapter

import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.LoadMoreItem
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter

abstract class LoadMoreAdapter: ListDelegationAdapter<MutableList<Entity>>() {
    init {
        delegatesManager.addDelegate(LoadMoreAdapterDelegate())
    }

    fun getItemAt(position: Int): Entity? {
        return if (position >= itemCount) null
        else items[position]
    }

    fun removeItem(position: Int){
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun setMoreItems(items: MutableList<Entity>){
        val lastSize = itemCount
        val length = items.size
        this.items.addAll(items)
        notifyItemRangeInserted(lastSize, length)

    }

    fun startLoadMore(){
        val count = itemCount
        items.add(LoadMoreItem())
        notifyItemInserted(count)
    }

    fun stopLoadMore(){
        val lastPosition = itemCount - 1
        val lastItem = items[lastPosition]
        if (lastItem is LoadMoreItem){
            items.removeAt(lastPosition)
            notifyItemRemoved(lastPosition)
        }
    }
}