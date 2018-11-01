package com.example.linh.vietkitchen.domain.mapper

import com.google.firebase.database.DataSnapshot

class TagsMapper {
    fun convertToDomain(dataSnapshot: DataSnapshot) : Map<String, Boolean>{
        val result = mutableMapOf<String, Boolean>()
        dataSnapshot.children.forEach {
            result[it.key.toString()] = true
        }
        return result
    }
}