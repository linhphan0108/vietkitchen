package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.cloud.Recipe
import com.example.linh.vietkitchen.data.response.PagingResponse
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.util.Constants.PAGINATION_LENGTH
import com.google.firebase.database.DataSnapshot

interface RecipeDataSource{
    suspend fun getAllRecipes(tag: String? = null, limit: Int = PAGINATION_LENGTH, startAtId: String? = null) : PagingResponse<List<DataSnapshot>>?
    suspend fun putRecipeWithDumpData(): Response<Boolean>?
    suspend fun putRecipe(recipe: Recipe): Response<String>?
    suspend fun updateRecipe(recipe: Recipe): Response<Boolean>?
    suspend fun uploadImages(multiPartFileList: List<ImageUpload>): Response<List<ImageUpload>>?
    suspend fun deleteImages(fileUrls: List<String>): Response<Boolean>?
    suspend fun getLikedRecipes(ids : List<String>): Response<List<DataSnapshot>>?
    suspend fun deleteRecipe(recipe: Recipe): Response<Boolean>?
}