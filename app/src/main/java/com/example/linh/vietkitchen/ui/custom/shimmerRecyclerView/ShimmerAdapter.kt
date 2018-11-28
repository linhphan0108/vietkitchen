package com.example.linh.vietkitchen.ui.custom.shimmerRecyclerView

import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter
import timber.log.Timber

private const val DEFAULT_NUMBER_OF_SHIMMER_ITEMS = 5

open class ShimmerAdapter : ListDelegationAdapter<MutableList<Entity>>(){
    private var isLoadingMore: Boolean = false
    var isShimmerAnimationReFresh: Boolean = false
    private var shimmerItemsCount = DEFAULT_NUMBER_OF_SHIMMER_ITEMS

    init {
        delegatesManager.addDelegate(ShimmerItemDelegate())
    }

    fun getItemAt(position: Int): Entity? {
        return if (position >= itemCount) null
        else items[position]
    }

    override fun setItems(items: MutableList<Entity>?) {
        super.setItems(items)
        if (items != null && items.isNotEmpty()){
            if (items[0] !is ShimmerItem){
                isShimmerAnimationReFresh = false
            }
        }
    }

    fun setMoreItems(items: MutableList<Entity>){
        val lastSize = itemCount
        val length = items.size
        this.items.addAll(items)
        notifyItemRangeInserted(lastSize, length)

    }

    fun removeItem(position: Int){
        items.removeAt(position)
        notifyItemRemoved(position)
    }

    fun startShimmerAnimation(numberShimmerItem: Int = shimmerItemsCount) {
        isShimmerAnimationReFresh = true
        shimmerItemsCount = numberShimmerItem
        val shimmerItems = mutableListOf<ShimmerItem>()
        for (i in 1..shimmerItemsCount){
            shimmerItems.add(ShimmerItem())
        }
        setItems(shimmerItems.toMutableList())
        notifyDataSetChanged()
    }

    fun stopShimmerAnimation(){
        if (itemCount < shimmerItemsCount) return
        var counter = 0
        for (i in 0 until shimmerItemsCount){
            if (items[counter] is ShimmerItem) {
                items.removeAt(counter)
                notifyItemRemoved(counter)
            }else{
                counter++
            }
        }
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