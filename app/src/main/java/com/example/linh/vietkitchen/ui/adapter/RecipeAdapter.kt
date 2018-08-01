package com.example.linh.vietkitchen.ui.adapter

import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.ShimmerAdapter
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.ShimmerItemDelegate
import com.example.linh.vietkitchen.ui.model.Entity

class RecipeAdapter(items: MutableList<Entity> = mutableListOf()) : ShimmerAdapter() {
    init {
        delegatesManager.addDelegate(RecipeAdapterDelegate())
        delegatesManager.addDelegate(ShimmerItemDelegate())
        setItems(items)
    }

    fun updateItemThenNotify(items: MutableList<Entity>){
        setItems(items)
        notifyDataSetChanged()
    }
}