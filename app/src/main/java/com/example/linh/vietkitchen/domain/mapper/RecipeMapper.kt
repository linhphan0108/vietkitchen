package com.example.linh.vietkitchen.domain.mapper

import com.example.linh.vietkitchen.data.cloud.Recipe as RecipeData
import com.example.linh.vietkitchen.extension.toMapOfStringBoolean
import javax.inject.Inject
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.extension.toListOfStringOfKey

class RecipeMapper @Inject constructor() {
    fun convertToDomain(listData: List<RecipeData>): List<Recipe> {
        return if (listData.isNullOrEmpty()){
            listOf()
        }else {
            listData.map {
                convertToDomain(it)
            }
        }
    }

    fun convertToDomain(recipeData: RecipeData): Recipe {
        return with(recipeData){
            Recipe(id, name, intro, ingredient, spice,
                    preparation, processing, notes, categories.toListOfStringOfKey(),
                    tags, thumbUrl, imageUrl)
        }
    }

    fun toData(domain: Recipe): RecipeData {
        return with(domain){
            RecipeData(id, name, intro, ingredient, spice, preparation, processing, notes,
                    categories.toMapOfStringBoolean(), tags, thumbUrl, imageUrl)
        }
    }
}