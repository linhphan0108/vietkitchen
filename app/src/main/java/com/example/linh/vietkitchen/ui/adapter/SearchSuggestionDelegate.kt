package com.example.linh.vietkitchen.ui.adapter

import android.view.ViewGroup
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.hannesdorfmann.adapterdelegates3.AbsListItemAdapterDelegate

class SearchSuggestionDelegate(val listener: SearchSuggestionViewHolder.OnItemListeners): AbsListItemAdapterDelegate<SearchItem, Entity, SearchSuggestionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup): SearchSuggestionViewHolder {
        return SearchSuggestionViewHolder(parent, listener)
    }

    override fun isForViewType(item: Entity, items: MutableList<Entity>, position: Int): Boolean {
        return item is SearchItem
    }

    override fun onBindViewHolder(item: SearchItem, viewHolder: SearchSuggestionViewHolder, payloads: MutableList<Any>) {
        viewHolder.bindView(item)
    }
}