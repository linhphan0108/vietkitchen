package com.example.linh.vietkitchen.data.cloud.mapper

import com.example.linh.vietkitchen.data.cloud.Recipe
import com.google.firebase.database.DataSnapshot
import com.example.linh.vietkitchen.domain.model.Recipe as FoodDomain

class RecipeMapper(private val ingredientMapper: IngredientMapper = IngredientMapper()) {

    fun convertToDomain(children: Iterable<DataSnapshot>): List<FoodDomain> {
//        val foods: MutableList<FoodDomain> = mutableListOf()
        return children.map {
            convertToDomain(it)
        }
    }

    fun convertToDomain(dataSnapshot: DataSnapshot): FoodDomain {
        val f = dataSnapshot.getValue(Recipe::class.java)!!
        val id = dataSnapshot.key
        return FoodDomain(id, f.name, f.intro, ingredientMapper.convertToDomain(f.ingredient), f.spice,
                f.preliminaryProcessing, f.processing, f.cookingMethod, f.benefit,
                f.recommendedSeason, f.region, f.specialDay, f.imageUrl)
    }
}