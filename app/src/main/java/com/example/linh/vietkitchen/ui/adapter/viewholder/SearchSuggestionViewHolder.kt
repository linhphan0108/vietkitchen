package com.example.linh.vietkitchen.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.ViewGroup
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.model.SearchItem
import kotlinx.android.synthetic.main.item_search_suggestion.view.*

class SearchSuggestionViewHolder(val parent: ViewGroup, val listener: OnItemListeners)
    : androidx.recyclerview.widget.RecyclerView.ViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_search_suggestion, parent, false)) {

    fun bindView(item: SearchItem){
        itemView.txtRecipeName.text = item.query
        itemView.setOnClickListener{
            listener.onItemClick(item)
        }
    }

    interface OnItemListeners{
        fun onItemClick(item: SearchItem)
    }
}