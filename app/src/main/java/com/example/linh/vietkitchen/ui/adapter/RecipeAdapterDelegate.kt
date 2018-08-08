package com.example.linh.vietkitchen.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.Recipe
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate


open class RecipeAdapterDelegate(val listener: OnItemClickListener?) : AbsListItemAdapterDelegate<Recipe, Entity, RecipeViewHolder>() {

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int): Boolean {
        return items[position] is Recipe
    }

    override fun onBindViewHolder(item: Recipe, viewHolder: RecipeViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item, payloads)
    }

    override fun onCreateViewHolder(parent: ViewGroup): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.ctx).inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(itemView, listener)
    }
}