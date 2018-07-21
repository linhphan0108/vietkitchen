package com.example.linh.vietkitchen.data.cloud.mapper

import com.example.linh.vietkitchen.data.local.Ingredient
import com.example.linh.vietkitchen.domain.model.Ingredient as IngredientDomain

class IngredientMapper {
    fun convertToDomain(ingredient: Map<String, Ingredient>) = ingredient.mapValues {
            convertToDomain(it.key, it.value)
        }

    private fun convertToDomain(key: String, i: Ingredient) = IngredientDomain(key, i.quantity, i.unit)
}