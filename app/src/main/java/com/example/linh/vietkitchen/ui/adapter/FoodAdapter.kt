package com.example.linh.vietkitchen.ui.adapter

import com.hannesdorfmann.adapterdelegates3.*

class FoodAdapter(items: List<Any>) : ListDelegationAdapter<List<Any>>() {
    init {
        delegatesManager.addDelegate(FoodAdapterDelegate())
        setItems(items)
    }

    fun updateItemThenNotify(items: List<Any>){
        this.items = items
        notifyDataSetChanged()
    }
}