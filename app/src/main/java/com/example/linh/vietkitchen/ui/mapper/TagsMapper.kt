package com.example.linh.vietkitchen.ui.mapper

class TagsMapper {
    fun toUi(map: Map<String, Boolean>): List<String>{
        return map.keys.toList()
    }

    fun toDomain(tags: List<String>?): Map<String, Boolean> {
        return tags?.associate {
            Pair(it, true)
        } ?: mapOf()
    }
}