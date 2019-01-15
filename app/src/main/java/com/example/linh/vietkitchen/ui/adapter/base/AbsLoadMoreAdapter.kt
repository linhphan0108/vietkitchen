package com.example.linh.vietkitchen.ui.adapter.base

import com.example.linh.vietkitchen.ui.adapter.delegation.LoadMoreAdapterDelegate
import com.example.linh.vietkitchen.ui.adapter.diffUtil.LoadMoreDiffUtilCallback
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.LoadMoreItem

abstract class AbsLoadMoreAdapter:
        AsyncListEntityDifferDelegationAdapter() {
    init {
        delegatesManager.addDelegate(LoadMoreAdapterDelegate())
        addDiffUtilDelegate(LoadMoreDiffUtilCallback())
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
        val mutableList = mutableListOf<Entity>()
        mutableList.addAll(items)
        mutableList.add(LoadMoreItem())
        items = mutableList
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