package com.example.linh.vietkitchen.domain.mapper

import com.example.linh.vietkitchen.data.response.Response
import com.google.firebase.database.DataSnapshot
import javax.inject.Inject

class TagsMapper @Inject constructor() {

    fun convertToDomain(dataResponse: Response<DataSnapshot>): Response<Map<String, Boolean>> {
        val map = if (dataResponse.data != null) convertToDomain(dataResponse.data) else null
        return Response(dataResponse.code, map)
    }

    fun convertToDomain(dataSnapshot: DataSnapshot) : Map<String, Boolean>{
        val result = mutableMapOf<String, Boolean>()
        dataSnapshot.children.forEach {
            result[it.key.toString()] = true
        }
        return result
    }
}