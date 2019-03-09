package com.example.linh.vietkitchen.data.cloud

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.linh.vietkitchen.data.mapper.CategoryMapper
import com.example.linh.vietkitchen.data.response.ApiResponse
import com.example.linh.vietkitchen.data.response.ApiEmptyResponse
import com.example.linh.vietkitchen.data.response.ApiErrorResponse
import com.example.linh.vietkitchen.data.response.ApiSuccessResponse
import com.example.linh.vietkitchen.extension.addListenerForSingleValueEventAwait
import com.example.linh.vietkitchen.extension.setValueAwait
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.util.Constants.STORAGE_FOOD
import com.example.linh.vietkitchen.util.transform
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class CategoryCloudDs @Inject constructor(
        private val database: FirebaseDatabase,
        private val mapper: CategoryMapper){

    private val dbRef by lazy{ database.getReference(STORAGE_FOOD)}

    fun getCategories(): LiveData<ApiResponse<List<CategoryGroup>>> {
        return dbRef.addListenerForSingleValueEventAwait().transform{
            when(it){
                is ApiSuccessResponse -> {
                    ApiResponse.createSuccess(mapper.toData(it.data))
                }
                is ApiEmptyResponse -> {
                    ApiResponse.createEmpty()
                }
                is ApiErrorResponse -> {
                    ApiResponse.createError(it.errorMessage)
                }
            }
        }
    }

    fun updateCategories(category: Category): LiveData<ApiResponse<Boolean>> {
        return Transformations.map(dbRef.setValueAwait(category.groups)){
            when(it){
                is ApiSuccessResponse -> {ApiResponse.createSuccess(true)}
                is ApiEmptyResponse -> {ApiResponse.createError("unknown error")}
                is ApiErrorResponse -> {ApiResponse.createError(it.errorMessage)}
            }
        }
    }
}