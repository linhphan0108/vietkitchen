package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.google.firebase.database.DataSnapshot
import com.example.linh.vietkitchen.data.cloud.Recipe
import com.example.linh.vietkitchen.data.response.PagingResponse
import com.example.linh.vietkitchen.data.response.Response

class RecipeLocalDataSource : RecipeDataSource {
    override suspend fun deleteImages(fileUrls: List<String>): Response<Boolean>? {
        return null
    }

    override suspend fun deleteRecipe(recipe: Recipe): Response<Boolean>? {
        return null
    }

    override suspend fun uploadImages(multiPartFileList: List<ImageUpload>): Response<List<ImageUpload>>? = null

    override suspend fun putRecipe(recipe: Recipe): Response<String>? = null

    override suspend fun updateRecipe(recipe: Recipe): Response<Boolean>? = null

    override suspend fun getLikedRecipes(ids: List<String>): Response<List<DataSnapshot>>? = null

//    override fun getLikedRecipes(uid: String): Flowable<List<Recipe>>?  = null

    override suspend fun putRecipeWithDumpData(): Response<Boolean>? = null

    override suspend fun getRecipes(tag: String?, limit: Int, startAtId: String?): PagingResponse<List<DataSnapshot>>? = null
}