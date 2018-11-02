package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.cloud.Recipe as RecipeData
import com.example.linh.vietkitchen.util.Constants.PAGINATION_LENGTH
import com.google.firebase.database.DataSnapshot
import io.reactivex.Completable
import io.reactivex.Flowable

interface RecipeDataSource{
    fun getAllRecipes(tag: String? = null, limit: Int = PAGINATION_LENGTH, startAtId: String? = null) : Flowable<List<DataSnapshot>>?
    fun putRecipeWithDumpData(): Completable?
    fun putRecipe(recipe: RecipeData): Flowable<String>?
    fun uploadImages(multiPartFileList: List<ImageUpload>): Flowable<ImageUpload>?
//    fun getLikedRecipes(uid: String) : Flowable<List<Recipe>>?
    fun getLikedRecipes(ids : List<String>): Flowable<DataSnapshot>?
}