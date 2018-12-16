package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.*
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_LIKED_RECIPES_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_PATH
import com.example.linh.vietkitchen.util.ResponseCode.RESPONSE_SUCCESS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class UserCloudDataSource : UserDataSource{

    private val dbRef by lazy{
        FirebaseDatabase.getInstance().getReference(STORAGE_USER_PATH)}

    override suspend fun likeRecipe(uid: String, recipeKey: String): Response<String> {
        val newDbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH).child(recipeKey)
        val id = newDbRef.setValueAwait(true)
        return Response(RESPONSE_SUCCESS, id)
    }

    override suspend fun unLikeRecipe(uid: String, recipeKey: String): Response<Boolean> {
        val newDbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH).child(recipeKey)
        val isSuccess = newDbRef.removeValueAwait()
        return Response(RESPONSE_SUCCESS, isSuccess)
    }

    override suspend fun getLikedRecipesId(uid: String): Response<DataSnapshot> {
        val dbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH)
        val dataSnapshot = dbRef.addListenerForSingleValueEventAwait()
        return Response(RESPONSE_SUCCESS, dataSnapshot)
    }
}