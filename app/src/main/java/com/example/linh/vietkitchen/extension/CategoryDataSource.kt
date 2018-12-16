package com.example.linh.vietkitchen.extension

import com.example.linh.vietkitchen.data.cloud.Category
import com.example.linh.vietkitchen.data.response.Response
import com.google.firebase.database.DataSnapshot

interface CategoryDataSource {
    suspend fun getCategories(): Response<DataSnapshot>?
    suspend fun updateCategories(category: Category): Response<Boolean>?
}