package com.example.linh.vietkitchen.ui.adapter.diffUtil

import com.example.linh.vietkitchen.ui.adapter.base.DiffUtilEntityItemCallback
import com.example.linh.vietkitchen.ui.model.Entity

class DiffUtilEntityItemCallbackDelegateFallback: DiffUtilEntityItemCallback.DiffUtilEntityItemCallbackDelegate {
    override fun isForViewType(item: Entity): Boolean {
        return false
    }

    override fun areEntityTheSame(oldItem: Entity, newItem: Entity): Boolean {
        return false
    }

    override fun getEntityChangePayload(oldItem: Entity, newItem: Entity): Any? {
        return null
    }
}