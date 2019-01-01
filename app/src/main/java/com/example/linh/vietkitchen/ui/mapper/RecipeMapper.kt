package com.example.linh.vietkitchen.ui.mapper

import com.example.linh.vietkitchen.extension.generateAnnotationSpan
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.extension.toMapOfStringBoolean
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.domain.model.Ingredient as IngredientDomain
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeMapper(private val likedRecipes: List<String> = VietKitchenApp.userInfo.likedRecipesIds!!) {

    fun convertToUi(listDomain: List<RecipeDomain>, defaultHasLiked: Boolean = false): List<Recipe> {
        return listDomain.map {
            val ui = convertToUi(it)
            ui.hasLiked = if (defaultHasLiked) defaultHasLiked else hasLiked(ui.id!!)
            ui
        }
    }

    fun  toDomain(recipe: Recipe): RecipeDomain{
        with(recipe) {
            val mapTags = tags?.toMapOfStringBoolean() ?: mapOf<String, Boolean>()
            return RecipeDomain(id, name, intro.toString(), ingredient.toString(), spice.toString(),
                    preparation.toString(), processing.toString(), notes.toString(), categories,
                    mapTags, thumbUrl, imageUrl)
        }
    }



    private fun convertToUi(domain: RecipeDomain): Recipe {
        return Recipe(domain.id, domain.name, domain.intro?.generateAnnotationSpan(),
                domain.ingredient.generateAnnotationSpan(), domain.spice.generateAnnotationSpan(),
                domain.preparation.generateAnnotationSpan(), domain.processing.generateAnnotationSpan(),
                domain.notes?.generateAnnotationSpan(), domain.categories,
                domain.tags.toListOfStringOfKey(), domain.thumbUrl, domain.imageUrl,false
        )
    }

//    private fun generateAnnotationSpan(source: String): CharSequence{
//        val ssb = SpannableStringBuilder()
//        source.forEach {
//            //steps
//            val trimmed = it.step.trim()
//            val start = ssb.length
//            val end = start + trimmed.length
//            ssb.append(trimmed.capitalize())
//            val a = Annotation("p", "start")
//            ssb.setSpan(a, start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//
//            //image
//            if (!it.imageUrl.isBlank()){
//                val holder = "&#2228;"
//                val startOffset = ssb.length
//                val endOffset = startOffset + holder.length
//                ssb.append(holder)
//                val imgAnnotation = Annotation("src", it.imageUrl)
//                ssb.setSpan(imgAnnotation, startOffset, endOffset, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
//            }
//        }
//        return ssb
//    }


    private fun hasLiked(recipeId: String) = likedRecipes.contains(recipeId)
}