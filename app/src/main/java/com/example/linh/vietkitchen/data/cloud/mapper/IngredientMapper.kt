package com.example.linh.vietkitchen.data.cloud.mapper

import com.example.linh.vietkitchen.data.cloud.Ingredient
import com.example.linh.vietkitchen.domain.model.Ingredient as IngredientDomain

class IngredientMapper {
    fun convertToDomain(ingredient: Map<String, Ingredient>) = ingredient.mapValues {
            convertToDomain(it.value)
        }

    private fun convertToDomain(i: Ingredient) = IngredientDomain(i.note, i.quantity, i.unit)
}