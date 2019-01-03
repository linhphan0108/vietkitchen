package com.example.linh.vietkitchen.ui.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.extension.lookTemporary
import com.example.linh.vietkitchen.ui.custom.likeButton.AndroidLikeButton
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.util.GlideUtil
import kotlinx.android.synthetic.main.item_recipe.view.*

class RecipeViewHolder(itemView: View, val listener: OnItemClickListener?) : RecyclerView.ViewHolder(itemView) {

    fun bindView(recipe: Recipe, payloads: MutableList<Any>) {
        if (payloads.size > 0){
            payloads.forEach {
                if(it is PayLoads) {
                    when (it) {
                        PayLoads.LIKE_CHANGE ->
                            itemView.btnFavorite.setCurrentlyLiked(recipe.hasLiked)
                    }
                }
            }
        }else {
            with(recipe) {
                itemView.imgCover.scaleType = ImageView.ScaleType.CENTER_CROP
                GlideUtil.widthLoadingHolder(itemView.context, itemView.imgCover, recipe.thumbUrl)
                        .override(540, 540)
                        .into(itemView.imgCover)
                itemView.txtFoodName.text = name
                itemView.txtShortIntro.text = intro
                itemView.btnFavorite.setCurrentlyLiked(hasLiked)
            }

            bindListeners(recipe)
        }
    }

    private fun bindListeners(recipe: Recipe) {
        itemView.setOnClickListener {
            listener?.onItemClick(itemView.imgCover, layoutPosition, adapterPosition, recipe)
        }
        if(BuildConfig.IS_ADMIN) {
            itemView.setOnLongClickListener {
                listener?.onItemLongClick(itemView.imgCover, layoutPosition, adapterPosition, recipe)
                        ?: false
            }
        }
        itemView.btnFavorite.setOnLikeEventListener(object: AndroidLikeButton.OnLikeEventListener {
            override fun onUnlikeClicked(androidLikeButton: AndroidLikeButton) {
                itemView.btnFavorite.lookTemporary()
                listener?.onUnLike(itemView.btnFavorite, layoutPosition, adapterPosition, recipe)
            }

            override fun onLikeClicked(androidLikeButton: AndroidLikeButton) {
                itemView.btnFavorite.lookTemporary()
                listener?.onLike(itemView.btnFavorite, layoutPosition, adapterPosition, recipe)
            }
        })
    }
}

enum class PayLoads{
    LIKE_CHANGE
}

interface OnItemClickListener{
    fun onItemClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe)
    fun onItemLongClick(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe): Boolean
    fun onLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe)
    fun onUnLike(itemView: View, layoutPosition: Int, adapterPosition: Int, data: Recipe)
}