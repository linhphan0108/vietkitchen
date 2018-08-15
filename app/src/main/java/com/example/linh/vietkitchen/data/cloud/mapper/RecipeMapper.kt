package com.example.linh.vietkitchen.data.cloud.mapper

import com.example.linh.vietkitchen.data.cloud.Recipe
import com.example.linh.vietkitchen.domain.model.ProcessStep
import com.google.firebase.database.DataSnapshot
import com.example.linh.vietkitchen.domain.model.Recipe as FoodDomain

class RecipeMapper(private val ingredientMapper: IngredientMapper = IngredientMapper()) {

    fun convertToDomain(children: Iterable<DataSnapshot>): List<FoodDomain> {
        return if (children.none()){
            listOf()
        }else {
            children.map {
                convertToDomain(it)
            }
        }
    }

    fun convertToDomain(dataSnapshot: DataSnapshot): FoodDomain {
        val f = dataSnapshot.getValue(Recipe::class.java)!!
        val id = dataSnapshot.key
        val preProcess = f.preliminaryProcessing.map {
            ProcessStep(it.step, it.imageUrl)
        }
        val process = f.processing.map {
            ProcessStep(it.step, it.imageUrl)
        }
        return FoodDomain(id, f.name, f.intro, ingredientMapper.convertToDomain(f.ingredient), f.spice,
                preProcess, process, f.cookingMethod, f.benefit,
                f.recommendedSeason, f.region, f.specialDay, f.thumbUrl, f.imageUrl)
    }
}