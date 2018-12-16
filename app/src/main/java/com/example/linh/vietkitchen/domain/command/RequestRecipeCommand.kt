package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.PagingResponse
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.util.Constants

class RequestRecipeCommand(var category: String? = null, private var limit: Int = Constants.PAGINATION_LENGTH,
                           var startAtId: String? = null,
                           private val provider: RecipeProvider = RecipeProvider())
    : CommandCoroutines<PagingResponse<List<Recipe>>>{

    override suspend fun executeOnTheInternet(context: Context): PagingResponse<List<Recipe>> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): PagingResponse<List<Recipe>> {
        return provider.requestFoods(category, limit, startAtId)
    }
}