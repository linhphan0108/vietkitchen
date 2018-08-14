package com.example.linh.vietkitchen.ui.screen.home.mapper

import android.text.*
import android.text.Annotation
import com.example.linh.vietkitchen.domain.model.ProcessStep
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.model.Ingredient
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

        val pre = generateAnnotationSpan(domain.preliminaryProcessing)
        val processSteps = generateAnnotationSpan(domain.processing)

        return Recipe(domain.id, domain.name, domain.intro, ingredient,
                domain.spice, pre, processSteps,
                domain.cookingMethod, domain.benefit, domain.recommendedSeason,
                domain.region, domain.specialDay, domain.imageUrl, false
        )
    }

    private fun generateAnnotationSpan(listText: List<ProcessStep>): CharSequence{
        val ssb = SpannableStringBuilder()
        listText.forEach {
            //steps
            val trimmed = it.step.trim()
            val start = ssb.length
            val end = start + trimmed.length
            ssb.append(trimmed.capitalize())
            val a = Annotation("p", "start")
            ssb.setSpan(a, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            //image
            if (!it.imageUrl.isBlank()){
                val holder = "&#2228;"
                val startOffset = ssb.length
                val endOffset = startOffset + holder.length
                ssb.append(holder)
                val imgAnnotation = Annotation("src", it.imageUrl)
                ssb.setSpan(imgAnnotation, startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
        }
        return ssb
    }

    private fun hasLiked(recipeId: String) = likedRecipes.contains(recipeId)
}