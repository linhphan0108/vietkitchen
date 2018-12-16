package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider

class PutRecipeCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandCoroutines<Response<String>>{
    lateinit var recipe: Recipe

    override suspend fun executeOnTheInternet(context: Context): Response<String> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute() = provider.putRecipe(recipe)

}