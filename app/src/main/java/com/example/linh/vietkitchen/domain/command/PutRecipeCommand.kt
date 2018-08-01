package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.provider.RecipeProvider

class PutRecipeCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandCompletable{
    override fun execute() = provider.putFood()

}