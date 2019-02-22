package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class UpdateRecipeCommand @Inject constructor(private val provider: RecipeProvider)
    : CommandCoroutines<Boolean>{
    lateinit var recipe: Recipe
    override fun execute() = provider.updateRecipe(recipe)

    override fun execute(context: Context): LiveData<Resource<Boolean>> {
        isInternetOn(context)
        return execute()
    }
}