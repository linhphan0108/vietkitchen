package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import io.reactivex.Flowable

class PutRecipeCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandFollowable<String>{
    lateinit var recipe: Recipe

    override fun executeOnTheInternet(context: Context): Flowable<String> {
        return isInternetOn(context)
                .flatMap { execute() }
    }

    override fun execute() = provider.putFood(recipe)

}