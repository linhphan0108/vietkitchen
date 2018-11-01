package com.example.linh.vietkitchen.domain.datasource

import android.net.Uri
import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource.MessageUploadCommunication
import com.example.linh.vietkitchen.data.cloud.Recipe as RecipeData
import com.example.linh.vietkitchen.util.Constants.PAGINATION_LENGTH
import com.google.firebase.database.DataSnapshot
import io.reactivex.Completable
import io.reactivex.Flowable

interface RecipeDataSource{
    fun getAllRecipes(tag: String? = null, limit: Int = PAGINATION_LENGTH, startAtId: String? = null) : Flowable<List<DataSnapshot>>?
    fun putRecipeWithDumpData(): Completable?
    fun putRecipe(recipe: RecipeData): Flowable<String>?
    fun uploadImages(multiPartFileMap: Map<String, Uri>): Flowable<MessageUploadCommunication>?
//    fun getLikedRecipes(uid: String) : Flowable<List<Recipe>>?
    fun getLikedRecipes(ids : List<String>): Flowable<DataSnapshot>?
}