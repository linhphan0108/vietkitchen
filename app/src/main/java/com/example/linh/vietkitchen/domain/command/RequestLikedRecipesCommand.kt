package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import io.reactivex.Flowable

class RequestLikedRecipesCommand(private val provider: RecipeProvider = RecipeProvider())
    : CommandFollowable<List<Recipe>> {
    var ids: List<String>? = null

    override fun executeOnTheInternet(context: Context): Flowable<out List<Recipe>> {
        return isInternetOn(context)
                .flatMap { execute() }
    }

    override fun execute(): Flowable<out List<Recipe>> {
        return if (ids != null && ids!!.isNotEmpty()){
            provider.requestLikedRecipes(ids!!)
        }else {
            throw NullPointerException("ids must be not null or empty")
        }
    }
}