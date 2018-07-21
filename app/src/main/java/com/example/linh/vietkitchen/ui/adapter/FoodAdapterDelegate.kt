package com.example.linh.vietkitchen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.extension.ctx
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate


open class FoodAdapterDelegate : AbsListItemAdapterDelegate<Food, Any, FoodViewHolder>() {

    override fun isForViewType(item: Any, items: MutableList<Any>, position: Int): Boolean {
        return items[position] is Food
    }

    override fun onBindViewHolder(item: Food, viewHolder: FoodViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item, payloads)
    }

    override fun onCreateViewHolder(parent: ViewGroup): FoodViewHolder {
        val itemView = LayoutInflater.from(parent.ctx).inflate(R.layout.item_food, parent, false)
        return FoodViewHolder(itemView)
    }
}