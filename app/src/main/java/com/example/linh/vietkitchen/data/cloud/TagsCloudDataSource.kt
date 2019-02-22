package com.example.linh.vietkitchen.data.cloud

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.example.linh.vietkitchen.data.mapper.TagsMapper
import com.example.linh.vietkitchen.data.response.ApiResponse
import com.example.linh.vietkitchen.data.response.ApiEmptyResponse
import com.example.linh.vietkitchen.data.response.ApiErrorResponse
import com.example.linh.vietkitchen.data.response.ApiSuccessResponse
import com.example.linh.vietkitchen.extension.addListenerForSingleValueEventAwait
import com.example.linh.vietkitchen.extension.setValueAwait
import com.example.linh.vietkitchen.util.Constants
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class TagsCloudDataSource @Inject constructor(
        private val database: FirebaseDatabase,
        private val mapper: TagsMapper){

    private val dbRefRecipe by lazy { database.getReference(Constants.STORAGE_RECIPES_TAGS_PATH) }

    fun getTags(): LiveData<ApiResponse<Map<String, Boolean>>> {
        return Transformations.map(dbRefRecipe.addListenerForSingleValueEventAwait()){ apiResponse ->
            when(apiResponse){
                is ApiSuccessResponse -> {
                    ApiResponse.createSuccess(mapper.convertToDomain(apiResponse.data))
                }
                is ApiEmptyResponse -> { ApiResponse.createEmpty()}
                is ApiErrorResponse -> {ApiResponse.createError(apiResponse.errorMessage)}
            }

        }
    }

    fun putTags(tags: Map<String, Boolean>) : LiveData<ApiResponse<Boolean>> {
        var result = true
        tags.forEach {tag ->
            val apiResponse = dbRefRecipe.child(tag.key).setValueAwait(tag.value).value
            result = when(apiResponse){
                is ApiSuccessResponse -> {true}
                else -> {false}
            }
        }
        return MutableLiveData<ApiResponse<Boolean>>().apply {
            value = ApiResponse.createSuccess(result)
        }
    }
}