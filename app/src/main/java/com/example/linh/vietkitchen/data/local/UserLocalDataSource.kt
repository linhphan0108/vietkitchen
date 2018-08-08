package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.domain.datasource.UserDataSource
import io.reactivex.Completable
import io.reactivex.Flowable

class UserLocalDataSource : UserDataSource {
    override fun getLikedRecipesId(uid: String): Flowable<List<String>>?  = null

    override fun likeRecipe(uid: String, recipeKey: String): Completable?  = null

    override fun unLikeRecipe(uid: String, recipeKey: String): Completable? = null
}