package com.example.linh.vietkitchen.ui.adapter

import com.example.linh.vietkitchen.extension.findIndex
import com.example.linh.vietkitchen.ui.adapter.base.AbsLoadMoreAdapter
import com.example.linh.vietkitchen.ui.adapter.delegation.RecipeAdapterDelegate
import com.example.linh.vietkitchen.ui.adapter.diffUtil.RecipeDiffUtilCallback
import com.example.linh.vietkitchen.ui.adapter.viewholder.OnItemClickListener
import com.example.linh.vietkitchen.ui.adapter.viewholder.PayLoads
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe

class RecipeAdapter(items: List<Entity> = listOf(),
                    val listener: OnItemClickListener? = null) : AbsLoadMoreAdapter() {

    init {
        delegatesManager.addDelegate(RecipeAdapterDelegate(listener))
        diffUtilCallback.diffUtilDelegateManager.add(RecipeDiffUtilCallback())
        setItems(items)
    }

    fun refresh(){
        items = listOf()
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
        if (index > -1){
            (items[index] as Recipe).hasLiked = true
            notifyItemChanged(index, PayLoads.LIKE_CHANGE)
        }else{
            val mutableList = mutableListOf<Entity>()
            mutableList.add(recipe)
            mutableList.addAll(items)
            items = mutableList
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
        if (index > -1){
            if (shouldRemove){
                val mutableList = mutableListOf<Entity>()
                mutableList.addAll(items)
                mutableList.removeAt(index)
                items = mutableList
            }else {
                (items[index] as Recipe).hasLiked = false
                notifyItemChanged(index, PayLoads.LIKE_CHANGE)
            }
        }
    }
}