package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import javax.inject.Inject

class PutRecipeCommand @Inject constructor(private val provider: RecipeProvider) : CommandCoroutines<Response<String>>{
    lateinit var recipe: Recipe

    override suspend fun execute(context: Context): Response<String> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute() = provider.putRecipe(recipe)

}