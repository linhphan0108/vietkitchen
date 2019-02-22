package com.example.linh.vietkitchen.domain.datasource

import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.cloud.Recipe
import com.example.linh.vietkitchen.data.response.PagingResponse
import com.example.linh.vietkitchen.util.Constants.PAGINATION_LENGTH
import com.google.firebase.database.DataSnapshot

interface RecipeDataSource{
    suspend fun requestRecipesByCategory(category: String? = null, limit: Int = PAGINATION_LENGTH, startAtId: String? = null) : PagingResponse<List<DataSnapshot>>?
    suspend fun requestRecipesByTag(tag: String? = null, limit: Int = PAGINATION_LENGTH, startAtId: String? = null) : PagingResponse<List<DataSnapshot>>?
    suspend fun putRecipeWithDumpData(): LiveData<Boolean>?
    suspend fun putRecipe(recipe: Recipe): LiveData<String>?
    suspend fun updateRecipe(recipe: Recipe): LiveData<Boolean>?
    suspend fun uploadImages(multiPartFileList: List<ImageUpload>): LiveData<List<ImageUpload>>?
    suspend fun deleteImages(fileUrls: List<String>): LiveData<Boolean>?
    suspend fun getLikedRecipes(ids : List<String>): LiveData<List<DataSnapshot>>?
    suspend fun deleteRecipe(recipe: Recipe): LiveData<Boolean>?
}