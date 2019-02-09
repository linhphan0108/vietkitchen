package com.example.linh.vietkitchen.ui.mapper

import com.example.linh.vietkitchen.ui.model.SearchItem
import javax.inject.Inject

class TagMapper @Inject constructor(){
    fun toSearchItem(list: List<String>): List<SearchItem>{
        return list.map {
            SearchItem(it, SearchItem.SearchItemType.TAG)
        }
    }

    fun toUi(map: Map<String, Boolean>): List<String>{
        return map.keys.toList()
    }

    fun toDomain(tags: List<String>?): Map<String, Boolean> {
        return tags?.associate {
            Pair(it, true)
        } ?: mapOf()
    }
}