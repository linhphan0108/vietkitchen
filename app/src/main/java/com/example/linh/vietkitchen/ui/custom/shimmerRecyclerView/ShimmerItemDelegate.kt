package com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.ctx
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate

class ShimmerItemDelegate : AbsListItemAdapterDelegate<ShimmerItem, Entity, ShimmerItemViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): ShimmerItemViewHolder {
        val itemView = LayoutInflater.from(parent.ctx).inflate(R.layout.item_shimmer_viewholder, parent, false)
        return ShimmerItemViewHolder(itemView)
    }

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int): Boolean {
        return item is ShimmerItem
    }

    override fun onBindViewHolder(item: ShimmerItem, viewHolder: ShimmerItemViewHolder, payloads: MutableList<Any>) {
        viewHolder.startShimmerAnimation()
    }

    override fun onViewAttachedToWindow(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder) {
        (holder as? ShimmerItemViewHolder)?.onViewAttachedToWindow()
        super.onViewAttachedToWindow(holder)
    }
}