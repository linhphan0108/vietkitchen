package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import javax.inject.Inject

class RequestLikedRecipesCommand @Inject constructor(private val provider: RecipeProvider)
    : CommandCoroutines<Response<List<Recipe>>> {
    var ids: List<String>? = null

    override suspend fun execute(context: Context): Response<List<Recipe>> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<List<Recipe>> {
        return if (ids != null && ids!!.isNotEmpty()){
            provider.requestLikedRecipes(ids!!)
        }else {
            throw NullPointerException("ids must be not null or empty")
        }
    }
}