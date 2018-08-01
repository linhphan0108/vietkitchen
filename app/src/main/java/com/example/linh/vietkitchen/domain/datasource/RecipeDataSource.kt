package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.util.Constants.PAGINATION_LENGTH
import io.reactivex.Completable
import io.reactivex.Flowable

interface RecipeDataSource{
    fun getAllRecipes(tag: String? = null, limit: Int = PAGINATION_LENGTH, startAtId: String? = null) : Flowable<List<Recipe>>?
    fun putRecipeWithDumpData(): Completable?
}