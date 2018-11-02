package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource
import com.example.linh.vietkitchen.data.local.RecipeLocalDataSource
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.domain.mapper.RecipeMapper
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.LoggerUtil
import io.reactivex.Flowable
import timber.log.Timber

class RecipeProvider(private val mapper: RecipeMapper = RecipeMapper(),
        sources: List<RecipeDataSource> = SOURCES) : BaseProvider<RecipeDataSource>(sources){
    companion object {
        val SOURCES by lazy { listOf(RecipeLocalDataSource(), RecipeCloudDataSource()) }
    }

    fun requestFoods(tag: String? = null, limit: Int = Constants.PAGINATION_LENGTH, startAtId: String? = null) : Flowable<List<Recipe>> = requestToSources {
        it.getAllRecipes(tag, limit, startAtId)
                ?.map {listDataSnapshot ->
                    Timber.d("onFetchData data's length ${listDataSnapshot.count()}")
                    Timber.d("latest key ${listDataSnapshot.last().key}")
                    LoggerUtil.logThread()
                    mapper.convertToDomain(listDataSnapshot)
                }
    }

    fun putFood(recipe: Recipe): Flowable<String> = requestToSources{
        it.putRecipe(mapper.toData(recipe))
//        it.putRecipeWithDumpData()
    }

//    fun requestLikedRecipes(uid: String) = requestToSources {
//        it.getLikedRecipes(uid)
//    }

    fun requestLikedRecipes(ids: List<String>) = requestToSources {
        it.getLikedRecipes(ids)
                ?.map {dataSnapshot ->
                    mapper.convertToDomain(dataSnapshot)}
                ?.toList()?.toFlowable()
    }

    fun uploadImages(multiPartFileMap: List<ImageUpload>) = requestToSources {
        it.uploadImages(multiPartFileMap)
    }
}