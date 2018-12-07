package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import io.reactivex.Completable
import io.reactivex.Flowable

class DeleteRecipeCommand(private val recipeProvider: RecipeProvider = RecipeProvider()): CommandCompletable {
    lateinit var recipe: Recipe

    override fun executeOnTheInternet(context: Context): Completable {
        return isInternetOn(context)
                .flatMapCompletable { execute() }
    }

    override fun execute(): Completable {
        return recipeProvider.deleteRecipe(recipe)
    }
}