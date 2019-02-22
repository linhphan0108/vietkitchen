package com.example.linh.vietkitchen.domain.datasource

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot

interface TagsDataSource {
    suspend fun getTags() : LiveData<DataSnapshot>?
    suspend fun putTags(tags: Map<String, Boolean>) : LiveData<Boolean>?
}