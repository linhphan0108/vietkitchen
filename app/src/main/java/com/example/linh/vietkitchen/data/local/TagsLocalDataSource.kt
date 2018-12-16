package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.TagsDataSource
import com.google.firebase.database.DataSnapshot

class TagsLocalDataSource : TagsDataSource {
    override suspend fun getTags(): Response<DataSnapshot>? = null

    override suspend fun putTags(tags: Map<String, Boolean>): Response<Boolean>? = null
}