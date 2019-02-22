package com.example.linh.vietkitchen.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.cloud.Recipe
import javax.inject.Inject

class RecipeLocalDataSource @Inject constructor(){
    fun deleteImages(fileUrls: List<String>): LiveData<Boolean> {
        return MutableLiveData<Boolean>().apply { value = true }
    }

    fun deleteRecipe(recipe: String): LiveData<Boolean> {
        return MutableLiveData<Boolean>().apply { value = true }
    }

    fun uploadImages(multiPartFileList: List<ImageUpload>): LiveData<List<ImageUpload>>{
        return MutableLiveData<List<ImageUpload>>().apply { value = null }
    }

    fun putRecipe(recipe: Recipe): LiveData<String> {
        return MutableLiveData<String>().apply {
            value = null
        }
    }

    fun updateRecipe(recipe: Recipe): LiveData<Boolean>{
        return MutableLiveData<Boolean>().apply { value = true }
    }

    fun getLikedRecipes(ids: List<String>): LiveData<List<Recipe>>{
        return MutableLiveData<List<Recipe>>().apply { value = null }
    }

//    override fun getLikedRecipes(uid: String): Flowable<List<Recipe>>?  = null

//    suspend fun putRecipeWithDumpData(): Response<Boolean>? = null

    fun requestRecipesByCategory(category: String?, limit: Int, startAtId: String?): LiveData<List<Recipe>>{
        return MutableLiveData<List<Recipe>>().apply {
            value = null
        }
    }

    fun requestRecipesByTag(tag: String?, limit: Int, startAtId: String?): LiveData<List<Recipe>> {
        return MutableLiveData<List<Recipe>>().apply {
            value = null
        }
    }
}