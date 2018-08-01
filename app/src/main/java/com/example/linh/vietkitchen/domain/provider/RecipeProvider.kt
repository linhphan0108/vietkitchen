package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource
import com.example.linh.vietkitchen.data.local.RecipeLocalDataSource
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.util.Constants
import io.reactivex.Completable
import io.reactivex.Flowable

class RecipeProvider(sources: List<RecipeDataSource> = SOURCES) : BaseProvider<RecipeDataSource>(sources){
    companion object {
        val SOURCES by lazy { listOf(RecipeLocalDataSource(), RecipeCloudDataSource()) }
    }

    fun requestFoods(tag: String? = null, limit: Int = Constants.PAGINATION_LENGTH, startAtId: String? = null) : Flowable<List<Recipe>> = requestToSources {
        val result = it.getAllRecipes(tag, limit, startAtId)
        result
    }

    fun putFood(): Completable = requestToSources{
        it.putRecipeWithDumpData()
    }
}