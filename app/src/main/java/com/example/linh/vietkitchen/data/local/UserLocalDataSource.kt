package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.UserDataSource
import com.google.firebase.database.DataSnapshot

class UserLocalDataSource : UserDataSource {
    override suspend fun getLikedRecipesId(uid: String): Response<DataSnapshot>? = null

    override suspend fun likeRecipe(uid: String, recipeKey: String): Response<String>? = null

    override suspend fun unLikeRecipe(uid: String, recipeKey: String): Response<Boolean>? = null
}