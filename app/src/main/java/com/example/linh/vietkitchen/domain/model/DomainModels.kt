package com.example.linh.vietkitchen.domain.model

open class Ingredient(val notes: String?, val quantity: Int, val unit: String)
data class ProcessStep(val step: String = "", val imageUrl: String = "")

class Recipe(val id: String?, val name: String, val intro: String?, val ingredient: String, val spice: String,
             val preparation: String, val processing: String, val notes: String?, val categories: List<String>,
             val tags: Map<String, Boolean>, var thumbUrl: String, var imageUrl: String)

data class CategoryChild(val itemTitle: String, val path: String, val numberItems: Int)
data class CategoryGroup(val headerTile: String, val path: String, val numberItems: Int, val itemsList: List<CategoryChild>? = null)
