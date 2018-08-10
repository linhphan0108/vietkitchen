package com.example.linh.vietkitchen.domain.model

import com.example.linh.vietkitchen.ui.model.Entity

open class Ingredient(val notes: String?, val quantity: Int, val unit: String)
data class ProcessStep(val step: String = "", val imageUrl: String = "")

class Recipe(val id: String?, val name: String, val intro: String, val ingredient: Map<String, Ingredient>, val spice: String,
             val preliminaryProcessing: List<ProcessStep>, val processing: List<ProcessStep>, val cookingMethod: Map<String, Boolean>,
             val benefit: Map<String, Boolean>?, val recommendedSeason: Map<String, Boolean>, val region: String?, val specialDay: String?,
             val imageUrl: String) : Entity()

data class CategoryItem(val itemTitle: String)
data class CategoryGroup(val headerTile: String, val itemsList: List<CategoryItem>? = null)
