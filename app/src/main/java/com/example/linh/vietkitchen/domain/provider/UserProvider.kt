package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.UserCloudDataSource
import com.example.linh.vietkitchen.data.local.UserLocalDataSource
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.UserDataSource
import javax.inject.Inject

class UserProvider @Inject constructor(localDataSource: UserLocalDataSource, cloudDataSource: UserCloudDataSource)
    : BaseProvider<UserDataSource>(listOf(localDataSource, cloudDataSource)) {

    suspend fun likeRecipe(uid: String, recipeKey: String): Response<String> {
        return requestAllSources { it.likeRecipe(uid, recipeKey) }
    }

    suspend fun unLikeRecipe(uid: String, recipeKey: String): Response<Boolean> {
        return requestAllSources { it.unLikeRecipe(uid, recipeKey) }
    }

    suspend fun requestLikedRecipesId(uid: String) = requestFirstSources {
        val response = it.getLikedRecipesId(uid)
        response?.let {res ->
            val listIds = mutableListOf<String>()
            for (child in res.data!!.children) {
                child.key?.let { listIds.add(child.key!!) }
            }
            listIds
        }
    }
}