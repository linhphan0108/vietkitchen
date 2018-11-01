package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider

class PutRecipeCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandFollowable<String>{
    lateinit var recipe: Recipe
    override fun execute() = provider.putFood(recipe)

}