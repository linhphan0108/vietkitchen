package com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import timber.log.Timber

open class ShimmerAdapter : ListDelegationAdapter<MutableList<Entity>>(){
    private var isLoadingMore: Boolean = false
    var isShimmerAnimationReFresh: Boolean = false

    init {
        delegatesManager.addDelegate(ShimmerItemDelegate())
    }

    override fun setItems(items: MutableList<Entity>?) {
        super.setItems(items)
        if (items != null && items.isNotEmpty()){
            if (items[0] !is ShimmerItem){
                isShimmerAnimationReFresh = false
            }
        }
    }

    fun startShimmerAnimation(numberShimmerItem: Int = 6) {
        isShimmerAnimationReFresh = true
        val shimmerItems = mutableListOf<ShimmerItem>()
        for (i in 1..numberShimmerItem){
            shimmerItems.add(ShimmerItem())
        }
        setItems(shimmerItems.toMutableList())
        notifyDataSetChanged()
    }

    fun startLoadMoreAnimation(){
        if (isLoadingMore || isShimmerAnimationReFresh) {
            return
        }
        isLoadingMore = true
        val shimmerItem = ShimmerItem()
        val lastIndex = items.lastIndex
        items.add(lastIndex + 1, shimmerItem)
        notifyItemInserted(lastIndex)
    }

    fun stopLoadMoreAnimation(){
        if (!isLoadingMore) return
        val lastIndex = items.lastIndex
        if (items[lastIndex] is ShimmerItem){
            items.removeAt(lastIndex)
            notifyItemRemoved(lastIndex)
        }
        isLoadingMore = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        Timber.e("onCreateViewHolder")
        return super.onCreateViewHolder(parent, viewType)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        Timber.e("onBindViewHolder")
        super.onBindViewHolder(holder, position)
    }
}

//region inner classes

//endregion inner classes