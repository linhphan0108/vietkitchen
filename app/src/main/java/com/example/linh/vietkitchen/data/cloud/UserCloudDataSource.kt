package com.example.linh.vietkitchen.data.cloud

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.linh.vietkitchen.data.response.ApiResponse
import com.example.linh.vietkitchen.data.response.ApiSuccessResponse
import com.example.linh.vietkitchen.extension.addListenerForSingleValueEventAwait
import com.example.linh.vietkitchen.extension.removeValueAwait
import com.example.linh.vietkitchen.extension.setValueAwait
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_LIKED_RECIPES_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_PATH
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class UserCloudDataSource @Inject constructor
(private val database: FirebaseDatabase){

    private val dbRef by lazy{database.getReference(STORAGE_USER_PATH)}

    fun likeRecipe(uid: String, recipeKey: String): LiveData<ApiResponse<String>> {
        val newDbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH).child(recipeKey)
        val timeStamp = System.currentTimeMillis()
       return newDbRef.setValueAwait(timeStamp)
    }

    fun unLikeRecipe(uid: String, recipeKey: String): LiveData<ApiResponse<Boolean>> {
        val newDbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH).child(recipeKey)
        return newDbRef.removeValueAwait()
    }

    fun getLikedRecipesId(uid: String): LiveData<ApiResponse<List<String>>>{
        val dbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH)
        val query = dbRef.orderByKey()
        val apiResponse = query.addListenerForSingleValueEventAwait()
        return Transformations.map(apiResponse){ response ->
            val listIds = mutableListOf<String>()
            when(response){
                is ApiSuccessResponse -> {
                    val snapshot = response.data
                    for (child in snapshot.children) {
                        child.key?.let { listIds.add(child.key!!) }
                    }
                }
            }
            ApiResponse.createSuccess(if (listIds.isEmpty()) null else listIds.toList())
        }
    }
}