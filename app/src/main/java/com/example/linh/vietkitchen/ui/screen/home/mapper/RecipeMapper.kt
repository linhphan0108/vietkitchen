package com.example.linh.vietkitchen.ui.screen.home.mapper

import android.text.Html
import android.text.Spannable
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.model.Ingredient
import com.example.linh.vietkitchen.ui.model.ProcessStep
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeMapper(private val likedRecipes: List<String> = VietKitchenApp.userInfo.likedRecipesIds!!) {

    fun convertToUi(listDomain: List<RecipeDomain>, defaultHasLiked: Boolean = false): List<Recipe> {
        return listDomain.map {
            val ui = convertToUi(it)
            ui.hasLiked = if (defaultHasLiked) defaultHasLiked else hasLiked(ui.id!!)
            ui
        }
    }

    private fun convertToUi(domain: RecipeDomain): Recipe {
        val ingredient = domain.ingredient.mapValues {
            val ingredientDomain = it.value
            Ingredient(ingredientDomain.notes, ingredientDomain.quantity, ingredientDomain.unit)
        }

        val pre = StringBuilder()
        domain.preliminaryProcessing.forEach {
            pre.append("<p>${it.step}<\\p>")
            if (!it.imageUrl.isNullOrBlank())
                pre.append("<img src=\"${it.imageUrl}\" >")
        }

        val processSteps = StringBuilder()
        domain.processing.forEach {
            processSteps.append("<p>${it.step}<\\p>")
            if (!it.imageUrl.isNullOrBlank())
                processSteps.append("<img src=\"${it.imageUrl}\" >")
        }

        return Recipe(domain.id, domain.name, domain.intro, ingredient,
                domain.spice, pre.toString(), processSteps.toString(),
                domain.cookingMethod, domain.benefit, domain.recommendedSeason,
                domain.region, domain.specialDay, domain.imageUrl, false
        )
    }

    private fun hasLiked(recipeId: String) = likedRecipes.contains(recipeId)
}