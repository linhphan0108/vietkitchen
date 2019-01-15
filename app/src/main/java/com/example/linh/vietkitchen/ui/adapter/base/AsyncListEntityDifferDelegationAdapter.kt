package com.example.linh.vietkitchen.ui.adapter.base

import com.example.linh.vietkitchen.ui.adapter.diffUtil.DiffUtilEntityItemCallbackDelegateFallback
import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates4.AsyncListDifferDelegationAdapter

abstract class AsyncListEntityDifferDelegationAdapter(
        protected val diffUtilCallback: DiffUtilEntityItemCallback = DiffUtilEntityItemCallback())
    : AsyncListDifferDelegationAdapter<Entity>(diffUtilCallback){
    init {
        addDiffUtilDelegate(DiffUtilEntityItemCallbackDelegateFallback())
    }

    protected fun addDiffUtilDelegate(diffUtil: DiffUtilEntityItemCallback.DiffUtilEntityItemCallbackDelegate){
        diffUtilCallback.diffUtilDelegateManager.add(diffUtil)
    }
}