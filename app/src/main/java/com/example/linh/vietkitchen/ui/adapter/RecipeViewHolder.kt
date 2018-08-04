package com.example.linh.vietkitchen.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.ui.GlideApp
import kotlinx.android.synthetic.main.item_recipe.view.*

class RecipeViewHolder(itemView: View, val listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

    fun bindView(recipe: Recipe, payloads: MutableList<Any>) {
        with(itemView) {
            GlideApp.with(itemView.context)
                    .load(recipe.imageUrl)
                    .into(imgFoodThumb)
            txtFoodName.text = recipe.name
            txtShortIntro.text = recipe.intro

        }
        itemView.setOnClickListener{
            listener?.onItemClick(itemView.imgFoodThumb, layoutPosition, adapterPosition, recipe)
        }
    }


}

interface OnItemClickListener{
    fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe)
}