package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource
import com.example.linh.vietkitchen.data.local.RecipeLocalDataSource
import com.example.linh.vietkitchen.data.response.PagingResponse
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.example.linh.vietkitchen.domain.mapper.RecipeMapper
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.ResponseCode
import com.example.linh.vietkitchen.util.TimberUtils
import timber.log.Timber

class RecipeProvider(private val mapper: RecipeMapper = RecipeMapper(),
        sources: List<RecipeDataSource> = SOURCES) : BaseProvider<RecipeDataSource>(sources){
    companion object {
        val SOURCES by lazy { listOf(RecipeLocalDataSource(), RecipeCloudDataSource()) }
    }

    suspend fun requestFoods(tag: String? = null, limit: Int = Constants.PAGINATION_LENGTH,
                             startAtId: String? = null) : PagingResponse<List<Recipe>>
            = requestFirstSources {
        val pagingResponse = it.getRecipes(tag, limit, startAtId)
        pagingResponse?.let { pagingRes ->
            TimberUtils.checkNotMainThread()
            val listDataSnapshot = pagingRes.data
            val listRecipes = listDataSnapshot?.let {
                Timber.d("onFetchData data's length ${listDataSnapshot.count()}")
                Timber.d("latest key ${listDataSnapshot.last().key}")
                mapper.convertToDomain(listDataSnapshot) }
            PagingResponse(pagingRes.code, listRecipes, pagingRes.isEnd, pagingRes.lastId)
        }
    }

    suspend fun putRecipe(recipe: Recipe): Response<String> = requestAllSources{
        it.putRecipe(mapper.toData(recipe))
    }

    suspend fun updateRecipe(recipe: Recipe): Response<Boolean> = requestAllSources {
        it.updateRecipe(mapper.toData(recipe))
    }

//    fun requestLikedRecipes(uid: String) = requestToSources {
//        it.getLikedRecipes(uid)
//    }

    suspend fun requestLikedRecipes(ids: List<String>) = requestFirstSources {
        val response = it.getLikedRecipes(ids)
        response?.let {
            Response(ResponseCode.RESPONSE_SUCCESS, mapper.convertToDomain(response.data!!))
        }
    }

    suspend fun uploadImages(multiPartFileMap: List<ImageUpload>) = requestAllSources {
        it.uploadImages(multiPartFileMap)
    }

    suspend fun deleteImages(fileUrls: List<String>) = requestAllSources {
        it.deleteImages(fileUrls)
    }

    suspend fun deleteRecipe(recipe: Recipe): Response<Boolean> = requestAllSources {
        it.deleteRecipe(mapper.toData(recipe))
    }
}