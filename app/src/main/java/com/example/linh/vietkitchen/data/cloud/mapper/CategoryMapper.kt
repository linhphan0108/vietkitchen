package com.example.linh.vietkitchen.data.cloud.mapper

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.model.CategoryItem
import com.google.firebase.database.DataSnapshot

class CategoryMapper{
    fun convertToDomain(categorySnapshot: DataSnapshot): List<CategoryGroup>{
        val categoryGroups = mutableListOf<CategoryGroup>()
        categorySnapshot.children.forEach {
            val categoryItems = mutableListOf<CategoryItem>()
            val groupSnapshot = it
            groupSnapshot.children.forEach{
                val itemTitle: String = it.key.toString()
                categoryItems.add(CategoryItem(itemTitle))
            }
            val groupTitle = groupSnapshot.key.toString()
            categoryGroups.add(CategoryGroup(groupTitle, categoryItems))
        }
        return categoryGroups
    }
}