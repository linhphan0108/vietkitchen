package com.example.linh.vietkitchen.ui.adapter.diffUtil

import com.example.linh.vietkitchen.ui.adapter.base.AbsDiffUtilEntityItemCallbackDelegate
import com.example.linh.vietkitchen.ui.adapter.viewholder.PayLoads
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe

class RecipeDiffUtilCallback : AbsDiffUtilEntityItemCallbackDelegate<Recipe>() {
    override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: Recipe, newItem: Recipe): Any? {
        if (oldItem.hasLiked != newItem.hasLiked){
            return PayLoads.LIKE_CHANGE
        }
        return null
    }

    override fun isForViewType(item: Entity): Boolean {
        return item is Recipe && item.id == item.id
    }
}