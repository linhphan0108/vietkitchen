package com.example.linh.vietkitchen.domain.datasource

import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.data.cloud.Category
import com.google.firebase.database.DataSnapshot

interface CategoryDataSource {
    suspend fun getCategories(): LiveData<DataSnapshot>?
    suspend fun updateCategories(category: Category): LiveData<Boolean>?
}