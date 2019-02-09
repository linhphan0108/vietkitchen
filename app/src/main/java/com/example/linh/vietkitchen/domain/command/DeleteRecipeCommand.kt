package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import javax.inject.Inject

class DeleteRecipeCommand @Inject constructor(private val recipeProvider: RecipeProvider): CommandCoroutines<Response<Boolean>> {

    lateinit var recipe: Recipe

    override suspend fun execute(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<Boolean> {
        return recipeProvider.deleteRecipe(recipe)
    }
}