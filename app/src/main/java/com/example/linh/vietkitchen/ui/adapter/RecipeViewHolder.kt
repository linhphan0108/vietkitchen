package com.example.linh.vietkitchen.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.example.linh.vietkitchen.extension.lookTemporary
import com.example.linh.vietkitchen.ui.GlideApp
import com.example.linh.vietkitchen.ui.model.Recipe
import com.like.LikeButton
import com.like.OnLikeListener
import kotlinx.android.synthetic.main.item_recipe.view.*

class RecipeViewHolder(itemView: View, val listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

    fun bindView(recipe: Recipe, payloads: MutableList<Any>) {
        if (payloads.size > 0){
            payloads.forEach {
                if(it is PayLoads) {
                    when (it) {
                        PayLoads.LIKE_CHANGE ->
                            itemView.btnFavorite.isLiked = recipe.hasLiked
                    }
                }
            }
        }else {
            with(recipe) {
                GlideApp.with(itemView.context)
                        .load(recipe.imageUrl)
                        .into(itemView.imgFoodThumb)
                itemView.txtFoodName.text = name
                itemView.txtShortIntro.text = intro
                itemView.btnFavorite.isLiked = hasLiked
            }

            bindListeners(recipe)
        }
    }

    private fun bindListeners(recipe: Recipe) {
        itemView.setOnClickListener {
            listener?.onItemClick(itemView.imgFoodThumb, layoutPosition, adapterPosition, recipe)
        }
        itemView.btnFavorite.setOnLikeListener(object : OnLikeListener {
            override fun liked(p0: LikeButton?) {
                itemView.btnFavorite.lookTemporary()
                listener?.onLike(itemView.btnFavorite, layoutPosition, adapterPosition, recipe)
            }

            override fun unLiked(p0: LikeButton?) {
                itemView.btnFavorite.lookTemporary()
                listener?.onUnLike(itemView.btnFavorite, layoutPosition, adapterPosition, recipe)
            }

        })
    }
}

enum class PayLoads{
    LIKE_CHANGE
}

interface OnItemClickListener{
    fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe)
    fun onLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe)
    fun onUnLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe)
}