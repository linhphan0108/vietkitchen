package com.example.linh.vietkitchen.domain.model

import com.example.linh.vietkitchen.ui.model.Entity

open class Ingredient(val notes: String?, val quantity: Int, val unit: String)
data class ProcessStep(val step: String = "", val imageUrl: String = "")

class Recipe(val id: String?, val name: String, val intro: String?, val ingredient: Map<String, Ingredient>, val spice: String,
             val preparation: String, val processing: String, val notes: String?, val categories: List<String>,
             val tags: Map<String, Boolean>, val thumbUrl: String, val imageUrl: String) : Entity()

data class CategoryChild(val itemTitle: String, val path: String, val numberItems: Int)
data class CategoryGroup(val headerTile: String, val path: String, val numberItems: Int, val itemsList: List<CategoryChild>? = null)
