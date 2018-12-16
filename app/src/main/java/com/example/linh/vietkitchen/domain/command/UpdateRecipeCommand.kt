package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider

class UpdateRecipeCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandCoroutines<Response<Boolean>>{
    lateinit var recipe: Recipe
    override suspend fun execute() = provider.updateRecipe(recipe)

    override suspend fun executeOnTheInternet(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }
}