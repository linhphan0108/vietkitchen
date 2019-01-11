package com.example.linh.vietkitchen.ui.adapter

import com.example.linh.vietkitchen.ui.model.Entity
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class SearchSuggestionAdapter(items: MutableList<Entity> = mutableListOf(),
                              val listener: SearchSuggestionViewHolder.OnItemListeners)
    : ListDelegationAdapter<MutableList<Entity>>(){
    init {
        delegatesManager.addDelegate(SearchSuggestionDelegate(listener))
        setItems(items)
    }

    override fun setItems(items: MutableList<Entity>){
        super.setItems(items)
        notifyDataSetChanged()
    }
}