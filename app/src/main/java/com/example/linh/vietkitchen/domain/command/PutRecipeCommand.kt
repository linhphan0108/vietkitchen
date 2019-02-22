package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class PutRecipeCommand @Inject constructor(private val provider: RecipeProvider)
    : CommandCoroutines<String>{
    lateinit var recipe: Recipe

    override fun execute(context: Context): LiveData<Resource<String>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute() = provider.putRecipe(recipe)

}