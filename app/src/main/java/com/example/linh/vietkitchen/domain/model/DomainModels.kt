package com.example.linh.vietkitchen.domain.model

open class Ingredient(val name: String, val quantity: Int, val unit: String)

data class Food(val id: String?, val name: String, val intro: String, val ingredient: Map<String, Ingredient>, val spice: String,
                val preliminaryProcessing: List<String>, val processing: List<String>, val cookingMethod: Map<String, Boolean>,
                val benefit: Map<String, Boolean>?, val recommendedSeason: Map<String, Boolean>, val region: String?, val specialDay: String?,
                val imageUrl: String)