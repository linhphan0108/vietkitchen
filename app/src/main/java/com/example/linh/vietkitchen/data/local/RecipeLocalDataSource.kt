package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.domain.model.Recipe
import io.reactivex.Completable
import io.reactivex.Flowable

class RecipeLocalDataSource : RecipeDataSource {
    override fun getLikedRecipes(ids: List<String>): Flowable<List<Recipe>>? = null

//    override fun getLikedRecipes(uid: String): Flowable<List<Recipe>>?  = null

    override fun putRecipeWithDumpData(): Completable? = null

    override fun getAllRecipes(tag: String?, limit: Int, startAtId: String?): Flowable<List<Recipe>>? = null
}