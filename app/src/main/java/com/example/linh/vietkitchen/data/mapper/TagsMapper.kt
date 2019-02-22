package com.example.linh.vietkitchen.data.mapper

import com.google.firebase.database.DataSnapshot
import javax.inject.Inject

class TagsMapper @Inject constructor() {
    fun convertToDomain(dataSnapshot: DataSnapshot) : Map<String, Boolean>{
        val result = mutableMapOf<String, Boolean>()
        dataSnapshot.children.forEach {
            result[it.key.toString()] = true
        }
        return result
    }
}