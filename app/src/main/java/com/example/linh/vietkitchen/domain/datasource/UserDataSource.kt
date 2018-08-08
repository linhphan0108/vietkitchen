package com.example.linh.vietkitchen.domain.datasource

import io.reactivex.Completable
import io.reactivex.Flowable

interface UserDataSource{
    fun likeRecipe(uid: String, recipeKey: String): Completable?
    fun unLikeRecipe(uid: String, recipeKey: String): Completable?
    fun getLikedRecipesId(uid: String): Flowable<List<String>>?
}