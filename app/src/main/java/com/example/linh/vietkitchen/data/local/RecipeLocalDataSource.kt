package com.example.linh.vietkitchen.data.local

import android.net.Uri
import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.google.firebase.database.DataSnapshot
import com.example.linh.vietkitchen.data.cloud.Recipe as RecipeData
import io.reactivex.Completable
import io.reactivex.Flowable

class RecipeLocalDataSource : RecipeDataSource {
    override fun uploadImages(multiPartFileMap: Map<String, Uri>): Flowable<RecipeCloudDataSource.MessageUploadCommunication>? = null

    override fun putRecipe(recipe: RecipeData): Flowable<String>? = null

    override fun getLikedRecipes(ids: List<String>): Flowable<DataSnapshot>? = null

//    override fun getLikedRecipes(uid: String): Flowable<List<Recipe>>?  = null

    override fun putRecipeWithDumpData(): Completable? = null

    override fun getAllRecipes(tag: String?, limit: Int, startAtId: String?): Flowable<List<DataSnapshot>>? = null
}