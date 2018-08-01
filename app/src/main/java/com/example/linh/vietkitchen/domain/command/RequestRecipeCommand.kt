package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.util.Constants
import io.reactivex.Flowable

class RequestRecipeCommand(var category: String? = null, private var limit: Int = Constants.PAGINATION_LENGTH,
                           var startAtId: String? = null,
                           private val provider: RecipeProvider = RecipeProvider())
    : CommandFollowable<List<Recipe>>{

    override fun execute(): Flowable<out List<Recipe>> {
        return provider.requestFoods(category, limit, startAtId)
    }
}