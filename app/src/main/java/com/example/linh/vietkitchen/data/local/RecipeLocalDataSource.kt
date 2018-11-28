package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.google.firebase.database.DataSnapshot
import com.example.linh.vietkitchen.data.cloud.Recipe
import io.reactivex.Completable
import io.reactivex.Flowable

class RecipeLocalDataSource : RecipeDataSource {
    override fun deleteImages(fileUrls: List<String>): Flowable<Boolean>? {
        return null
    }

    override fun deleteRecipe(recipe: Recipe): Completable? {
        return null
    }

    override fun uploadImages(multiPartFileList: List<ImageUpload>): Flowable<ImageUpload>? = null

    override fun putRecipe(recipe: Recipe): Flowable<String>? = null

    override fun getLikedRecipes(ids: List<String>): Flowable<DataSnapshot>? = null

//    override fun getLikedRecipes(uid: String): Flowable<List<Recipe>>?  = null

    override fun putRecipeWithDumpData(): Completable? = null

    override fun getAllRecipes(tag: String?, limit: Int, startAtId: String?): Flowable<List<DataSnapshot>>? = null
}