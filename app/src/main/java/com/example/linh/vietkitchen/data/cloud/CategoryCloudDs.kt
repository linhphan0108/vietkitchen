package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.extension.addListenerForSingleValueEventAwait
import com.example.linh.vietkitchen.extension.setValueAwait
import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.example.linh.vietkitchen.util.Constants.STORAGE_FOOD
import com.example.linh.vietkitchen.util.ResponseCode.RESPONSE_SUCCESS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class CategoryCloudDs @Inject constructor
(private val database: FirebaseDatabase) : CategoryDataSource {

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