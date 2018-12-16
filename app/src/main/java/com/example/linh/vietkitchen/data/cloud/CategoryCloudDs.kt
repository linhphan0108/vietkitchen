package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.addListenerForSingleValueEventAwait
import com.example.linh.vietkitchen.domain.datasource.setValueAwait
import com.example.linh.vietkitchen.extension.CategoryDataSource
import com.example.linh.vietkitchen.util.ResponseCode.RESPONSE_SUCCESS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class CategoryCloudDs : CategoryDataSource {
    companion object {
        const val STORAGE_FOOD = "category"
    }

    private val database  by lazy { FirebaseDatabase.getInstance()}
    private val dbRef by lazy{ database.getReference(STORAGE_FOOD)}

    override suspend fun getCategories(): Response<DataSnapshot> {
        val dataSnapshot = dbRef.addListenerForSingleValueEventAwait()
        return Response(RESPONSE_SUCCESS, dataSnapshot)
    }

    override suspend fun updateCategories(category: Category): Response<Boolean>? {
        val result = dbRef.setValueAwait(category.groups)
        return Response(RESPONSE_SUCCESS, !result.isNullOrBlank())
    }
}