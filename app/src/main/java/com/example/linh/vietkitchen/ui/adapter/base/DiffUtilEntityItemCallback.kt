package com.example.linh.vietkitchen.ui.adapter.base
import androidx.recyclerview.widget.DiffUtil
import com.example.linh.vietkitchen.ui.adapter.diffUtil.DiffUtilEntityItemCallbackDelegateFallback
import com.example.linh.vietkitchen.ui.model.Entity

class DiffUtilEntityItemCallback : DiffUtil.ItemCallback<Entity>() {
    internal val diffUtilDelegateManager: MutableList<DiffUtilEntityItemCallbackDelegate> =
            mutableListOf(DiffUtilEntityItemCallbackDelegateFallback())

    override fun areItemsTheSame(oldItem: Entity, newItem: Entity): Boolean {
        return oldItem.javaClass == newItem.javaClass && oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Entity, newItem: Entity): Boolean {
        @Suppress("UNCHECKED_CAST")
        val delegate = getDelegateIndexForItem(oldItem)
        return delegate.areEntityTheSame(oldItem, newItem)
    }

    override fun getChangePayload(oldItem: Entity, newItem: Entity): Any? {
        val delegate = getDelegateIndexForItem(oldItem)
        return delegate.getEntityChangePayload(oldItem, newItem)
    }

    private fun getDelegateIndexForItem(item: Entity): DiffUtilEntityItemCallbackDelegate {
        var delegate = diffUtilDelegateManager[0]
        for (i in 1 until diffUtilDelegateManager.size){
            if (diffUtilDelegateManager[i].isForViewType(item)){
                delegate = diffUtilDelegateManager[i]
            }
        }
        return delegate
    }

    interface DiffUtilEntityItemCallbackDelegate{
        fun isForViewType(item: Entity): Boolean
        fun areEntityTheSame(oldItem: Entity, newItem: Entity): Boolean
        fun getEntityChangePayload(oldItem: Entity, newItem: Entity): Any?
    }
}