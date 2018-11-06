package com.example.linh.vietkitchen.admin.ui.screen.admin

import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates3.ListDelegationAdapter

class CategoryCheckerAdapter(items: List<Entity> = listOf(),
                             listener: CategoryCheckerChildHolder.OnItemClickListener)
    : ListDelegationAdapter<List<Entity>>(){
    init {
        delegatesManager.addDelegate(CategoryCheckChildDelegate(listener))
        delegatesManager.addDelegate(CategoryCheckerGroupDelegate())
        setItems(items)
    }
}