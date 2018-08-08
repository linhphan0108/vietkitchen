package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.UserCloudDataSource
import com.example.linh.vietkitchen.data.local.UserLocalDataSource
import com.example.linh.vietkitchen.domain.datasource.UserDataSource
import io.reactivex.Completable

class UserProvider(sources: List<UserDataSource> = SOURCES) : BaseProvider<UserDataSource>(sources) {
    companion object {
        val SOURCES by lazy { listOf(UserLocalDataSource(), UserCloudDataSource()) }
    }

    fun likeRecipe(uid: String, recipeKey: String): Completable{
        return putToSources { it.likeRecipe(uid, recipeKey) }
    }

    fun unLikeRecipe(uid: String, recipeKey: String): Completable{
        return putToSources { it.unLikeRecipe(uid, recipeKey) }
    }

    fun requestLikedRecipesId(uid: String) = requestToSources {
        it.getLikedRecipesId(uid)
    }
}