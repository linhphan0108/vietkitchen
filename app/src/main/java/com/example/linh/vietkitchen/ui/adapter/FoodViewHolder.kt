package com.example.linh.vietkitchen.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.ui.GlideApp
import kotlinx.android.synthetic.main.item_food.view.*

class FoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    fun bindView(food: Food, payloads: MutableList<Any>) {
        with(itemView) {
            GlideApp.with(itemView.context)
                .load(food.imageUrl)
                .into(imgFoodThumb)
            txtFoodName.text = food.name
            txtShortIntro.text = food.intro

        }
    }
}