package com.example.linh.vietkitchen.domain.datasource

import androidx.lifecycle.LiveData
import com.google.firebase.database.DataSnapshot

interface UserDataSource{
    suspend fun likeRecipe(uid: String, recipeKey: String): LiveData<String>?
    suspend fun unLikeRecipe(uid: String, recipeKey: String): LiveData<Boolean>?
    suspend fun getLikedRecipesId(uid: String): LiveData<DataSnapshot>?
}