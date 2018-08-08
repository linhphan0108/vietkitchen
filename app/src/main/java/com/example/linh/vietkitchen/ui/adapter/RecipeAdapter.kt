package com.example.linh.vietkitchen.ui.adapter

import com.example.linh.vietkitchen.extension.findIndex
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.ShimmerAdapter
import com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView.ShimmerItemDelegate
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe

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

    fun onLike(recipe: Recipe){
        val id = recipe.id
        val index = items.findIndex {
            if (it is Recipe){
                it.id == id
            }else{
                false
            }
        }
        if (index > 0){
            (items[index] as Recipe).hasLiked = true
            notifyItemChanged(index, PayLoads.LIKE_CHANGE)
        }else{
            items.add(0, recipe)
            notifyItemInserted(0)
        }
    }

    fun onUnLike(recipe: Recipe, shouldRemove: Boolean = false){
        val id = recipe.id
        val index = items.findIndex {
            if (it is Recipe){
                it.id == id
            }else{
                false
            }
        }
        if (index >= 0){
            if (shouldRemove){
                items.removeAt(index)
                notifyItemRemoved(index)
            }else {
                (items[index] as Recipe).hasLiked = false
                notifyItemChanged(index, PayLoads.LIKE_CHANGE)
            }
        }
    }
}