package com.example.linh.vietkitchen.ui.adapter

import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.ShimmerAdapter
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.ShimmerItemDelegate
import com.example.linh.vietkitchen.ui.model.Entity

class RecipeAdapter(items: MutableList<Entity> = mutableListOf(),
                    val listener: OnItemClickListener? = null) : ShimmerAdapter() {
    init {
        delegatesManager.addDelegate(RecipeAdapterDelegate(listener))
        delegatesManager.addDelegate(ShimmerItemDelegate())
        setItems(items)
    }

    fun updateItemThenNotify(items: MutableList<Entity>){
        setItems(items)
        notifyDataSetChanged()
    }
}