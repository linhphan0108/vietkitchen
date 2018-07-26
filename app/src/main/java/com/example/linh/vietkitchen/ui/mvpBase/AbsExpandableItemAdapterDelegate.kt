package com.example.linh.vietkitchen.ui.mvpBase

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.RecyclerView.ViewHolder
import android.view.ViewGroup
import com.hannesdorfmann.adapterdelegates3.AdapterDelegate

abstract class AbsExpandableItemAdapterDelegate<P: T, C : T, T, PVH : ViewHolder, CVH: ViewHolder> :
        AdapterDelegate<List<T>>() {

    override fun isForViewType(items: List<T>, position: Int) = isForViewType(items[position], items, position)

    final override fun onCreateViewHolder(parent: ViewGroup?): ViewHolder {
        return if(isParentView()) {
            onCreateParentViewHolder(parent)
        } else{
            onCreateChildViewHolder(parent)
        }
    }

    @Suppress("UNCHECKED_CAST")
    final override fun onBindViewHolder(items: List<T>, position: Int, holder: ViewHolder, payloads: MutableList<Any>) {
        if (isParentView()){
            onBindParentViewHolder(items[position] as P, holder as PVH, payloads)
        }else{
            onBindChildViewHolder(items[position] as C, holder as CVH, payloads)
        }
    }

    /**
     * Called to determine whether this AdapterDelegate is the responsible for the given item in the
     * list or not
     * element
     *
     * @param item The item from the list at the given position
     * @param items The items from adapters dataset
     * @param position The items position in the dataset (list)
     * @return true if this AdapterDelegate is responsible for that, otherwise false
     */
    protected abstract fun isForViewType(item: T, items: List<T>, position: Int): Boolean

    protected abstract fun isParentView(): Boolean

    /**
     * Creates the  [RecyclerView.ViewHolder] for the given data source item
     *
     * @param parent The ViewGroup parent of the given datasource
     * @return ViewHolder
     */
    abstract fun onCreateParentViewHolder(parent: ViewGroup?): PVH
        abstract fun onCreateChildViewHolder(parent: ViewGroup?): CVH
    /**
     * Called to bind the [RecyclerView.ViewHolder] to the item of the dataset
     */
    abstract fun onBindParentViewHolder(item: P, holder: PVH, payloads: MutableList<Any>)
    /**
     * Called to bind the [RecyclerView.ViewHolder] to the item of the dataset
     */
    abstract fun onBindChildViewHolder(item: C, holder: CVH, payloads: MutableList<Any>)
}