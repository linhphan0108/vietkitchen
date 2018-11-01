package com.example.linh.vietkitchen.domain.mapper

import com.example.linh.vietkitchen.data.cloud.Ingredient
import com.example.linh.vietkitchen.data.cloud.Recipe
import com.google.firebase.database.DataSnapshot
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeMapper(private val ingredientMapper: IngredientMapper = IngredientMapper()) {

    fun convertToDomain(children: Iterable<DataSnapshot>): List<RecipeDomain> {
        return if (children.none()){
            listOf()
        }else {
            children.map {
                convertToDomain(it)
            }
        }
    }

    fun convertToDomain(dataSnapshot: DataSnapshot): RecipeDomain {
        val f = dataSnapshot.getValue(Recipe::class.java)!!
        val id = dataSnapshot.key
//        val preProcess = f.preparation.map {
//            ProcessStep(it.step, it.imageUrl)
//        }
//        val process = f.processing.map {
//            ProcessStep(it.step, it.imageUrl)
//        }
        return RecipeDomain(id, f.name, f.intro, ingredientMapper.convertToDomain(f.ingredient), f.spice,
                f.preparation, f.processing, f.cookingMethod, f.benefit,
                f.recommendedSeason, f.region, f.specialDay, f.tags, f.thumbUrl, f.imageUrl)
    }

    fun toData(domain: RecipeDomain): Recipe {
        with(domain){
            val ingredientDataMap = ingredient.mapValues {
                with(it.value){
                    Ingredient(quantity, unit, notes)
                }
            }
            val regionTemp = if (region.isNullOrBlank()) null else region
            val specialDayTemp = if (specialDay.isNullOrBlank()) null else specialDay
            return Recipe(name, intro, ingredientDataMap, spice, preparation, processing, cookingMethod,
                    benefit, recommendedSeason, regionTemp, specialDayTemp, tags, thumbUrl, imageUrl)
        }
    }
}