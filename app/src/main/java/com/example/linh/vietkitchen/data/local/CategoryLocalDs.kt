package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.data.cloud.Category
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.extension.CategoryDataSource
import com.google.firebase.database.DataSnapshot

class CategoryLocalDs : CategoryDataSource {
    override suspend fun updateCategories(category: Category): Response<Boolean>? {
        return null
    }

    override suspend fun getCategories(): Response<DataSnapshot>? {
        return null
    }

}