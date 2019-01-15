package com.example.linh.vietkitchen.ui.adapter.base

import com.example.linh.vietkitchen.ui.model.Entity

abstract class AbsDiffUtilEntityItemCallbackDelegate<T: Entity> : DiffUtilEntityItemCallback.DiffUtilEntityItemCallbackDelegate {

    final override fun areEntityTheSame(oldItem: Entity, newItem: Entity): Boolean {
        @Suppress("UNCHECKED_CAST")
        return areContentsTheSame(oldItem as T, newItem as T)
    }

    final override fun getEntityChangePayload(oldItem: Entity, newItem: Entity): Any? {
        @Suppress("UNCHECKED_CAST")
        return getChangePayload(oldItem as T, newItem as T)
    }

    abstract fun areContentsTheSame(oldItem: T, newItem: T): Boolean

    abstract fun getChangePayload(oldItem: T, newItem: T): Any?
}