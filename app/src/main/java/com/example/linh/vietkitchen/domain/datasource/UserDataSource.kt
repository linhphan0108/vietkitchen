package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.data.response.Response
import com.google.firebase.database.DataSnapshot

interface UserDataSource{
    suspend fun likeRecipe(uid: String, recipeKey: String): Response<String>?
    suspend fun unLikeRecipe(uid: String, recipeKey: String): Response<Boolean>?
    suspend fun getLikedRecipesId(uid: String): Response<DataSnapshot>?
}