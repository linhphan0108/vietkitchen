package com.example.linh.vietkitchen.data.cloud.mapper

import com.example.linh.vietkitchen.data.local.Food
import com.example.linh.vietkitchen.domain.model.Food as FoodDomain
import com.google.firebase.database.DataSnapshot

class FoodMapper(private val ingredientMapper: IngredientMapper = IngredientMapper()) {

    fun convertToDomain(children: Iterable<DataSnapshot>): List<FoodDomain> {
//        val foods: MutableList<FoodDomain> = mutableListOf()
        return children.map {
            val f = it.getValue(Food::class.java)!!
            val id = it.key
            FoodDomain(id, f.name, f.intro, ingredientMapper.convertToDomain(f.ingredient), f.spice,
                    f.preliminaryProcessing, f.processing, f.cookingMethod, f.benefit,
                    f.recommendedSeason, f.region, f.specialDay, f.imageUrl)
        }
    }
}