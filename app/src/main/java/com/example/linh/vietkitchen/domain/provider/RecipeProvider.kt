package com.example.linh.vietkitchen.domain.provider

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource
import com.example.linh.vietkitchen.data.local.RecipeLocalDataSource
import com.example.linh.vietkitchen.data.response.*
import com.example.linh.vietkitchen.domain.mapper.RecipeMapper
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.data.cloud.Recipe as RecipeData
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.ResponseCode
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class RecipeProvider @Inject constructor(
        private val mapper: RecipeMapper,
        private val localDataSource: RecipeLocalDataSource,
        private val cloudDataSource: RecipeCloudDataSource){

    fun requestRecipeByCategory(cat: String? = null, limit: Int = Constants.PAGINATION_LENGTH,
                                        startAtId: String? = null) : LiveData<Resource<PagingResponse<List<Recipe>>>>{
        return object : NetworkBoundResource<PagingResponse<List<Recipe>>, PagingResponse<List<RecipeData>>>(){
            override fun saveCallResult(item: PagingResponse<List<RecipeData>>) : PagingResponse<List<Recipe>>{
                val convertedList = item.data?.let {
                    mapper.convertToDomain(it)
                }
                return PagingResponse(convertedList, item.isEnd, item.lastId)
            }

            override fun callDb(): LiveData<PagingResponse<List<Recipe>>> {
                return Transformations.map(localDataSource.requestRecipesByCategory(cat, limit, startAtId)){ listRecipeData ->
                    listRecipeData?.let{
                        val list = mapper.convertToDomain(listRecipeData)
                        PagingResponse(list, false, null)
                    }
                }
            }

            override fun createCall(): LiveData<ApiResponse<PagingResponse<List<RecipeData>>>> {
                return cloudDataSource.requestRecipesByCategory(cat, limit, startAtId)
            }
        }.execute()
    }

    fun requestRecipeByTag(tag: String? = null, limit: Int = Constants.PAGINATION_LENGTH,
                                        startAtId: String? = null) : LiveData<Resource<PagingResponse<List<Recipe>>>> {
        return object : NetworkBoundResource<PagingResponse<List<Recipe>>, PagingResponse<List<RecipeData>>>(){
            override fun saveCallResult(item: PagingResponse<List<RecipeData>>): PagingResponse<List<Recipe>> {
                val convertedList = item.data?.let {
                    mapper.convertToDomain(it)
                }
                return PagingResponse(convertedList, item.isEnd, item.lastId)
            }

            override fun callDb(): LiveData<PagingResponse<List<Recipe>>> {
                return Transformations.map(localDataSource.requestRecipesByTag(tag, limit, startAtId)){
                    val list = mapper.convertToDomain(it)
                    PagingResponse(list, false, null)
                }
            }

            override fun createCall(): LiveData<ApiResponse<PagingResponse<List<RecipeData>>>> {
                return cloudDataSource.requestRecipesByTag(tag, limit, startAtId)
            }
        }.execute()
    }

    fun putRecipe(recipe: Recipe): LiveData<Resource<String>> {
        val data = mapper.toData(recipe)
        return object : PushNetworkResource<String, String>(){
            override fun saveCallResult(item: String): String {
                return item
            }

            override fun callDb(): LiveData<String> {
                return localDataSource.putRecipe(data)
            }

            override fun createCall(): LiveData<ApiResponse<String>> {
                return cloudDataSource.putRecipe(data)
            }
        }.execute()
    }

    fun updateRecipe(recipe: Recipe): LiveData<Resource<Boolean>> {
        val data = mapper.toData(recipe)
        return object : PushNetworkResource<Boolean, Boolean>(){
            override fun saveCallResult(item: Boolean): Boolean {
                return item
            }

            override fun callDb(): LiveData<Boolean> {
                return localDataSource.updateRecipe(data)
            }

            override fun createCall(): LiveData<ApiResponse<Boolean>> {
                return cloudDataSource.updateRecipe(data)
            }
        }.execute()
    }

//    fun requestLikedRecipes(uid: String) = requestToSources {
//        it.getLikedRecipes(uid)
//    }

    fun requestLikedRecipes(ids: List<String>): LiveData<Resource<List<Recipe>>> {
        return object : NetworkBoundResource<List<Recipe>, List<RecipeData>>(){
            override fun saveCallResult(item: List<RecipeData>): List<Recipe> {
                return mapper.convertToDomain(item)
            }

            override fun callDb(): LiveData<List<Recipe>> {
                return Transformations.map(localDataSource.getLikedRecipes(ids)){
                    mapper.convertToDomain(it)
                }
            }

            override fun createCall(): LiveData<ApiResponse<List<RecipeData>>> {
                return cloudDataSource.getLikedRecipes(ids)
            }
        }.execute()
    }

    fun uploadImages(multiPartFileMap: List<ImageUpload>): LiveData<Resource<List<ImageUpload>>> {
        return object : PushNetworkResource<List<ImageUpload>, List<ImageUpload>>(){
            override fun saveCallResult(item: List<ImageUpload>): List<ImageUpload> {
                return item
            }

            override fun callDb(): LiveData<List<ImageUpload>> {
                return localDataSource.uploadImages(multiPartFileMap)
            }

            override fun createCall(): LiveData<ApiResponse<List<ImageUpload>>> {
                return cloudDataSource.uploadImages(multiPartFileMap)
            }
        }.execute()
    }

    fun deleteImages(fileUrls: List<String>): LiveData<Resource<Boolean>> {
        return object : PushNetworkResource<Boolean, Boolean>(){
            override fun saveCallResult(item: Boolean): Boolean {
                return item
            }

            override fun callDb(): LiveData<Boolean> {
                return localDataSource.deleteImages(fileUrls)
            }

            override fun createCall(): LiveData<ApiResponse<Boolean>> {
                return cloudDataSource.deleteImages(fileUrls)
            }
        }.execute()
    }

    fun deleteRecipe(recipe: Recipe): LiveData<Resource<Boolean>> {
        val id = recipe.id!!
        return object : PushNetworkResource<Boolean, Boolean>(){
            override fun saveCallResult(item: Boolean): Boolean {
                return item
            }

            override fun callDb(): LiveData<Boolean> {
                return localDataSource.deleteRecipe(id)
            }

            override fun createCall(): LiveData<ApiResponse<Boolean>> {
                return cloudDataSource.deleteRecipe(id)
            }
        }.execute()
    }
}