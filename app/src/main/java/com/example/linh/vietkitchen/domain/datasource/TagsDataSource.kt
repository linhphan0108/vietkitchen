package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.data.response.Response
import com.google.firebase.database.DataSnapshot

interface TagsDataSource {
    suspend fun getTags() : Response<DataSnapshot>?
    suspend fun putTags(tags: Map<String, Boolean>) : Response<Boolean>?
}