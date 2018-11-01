package com.example.linh.vietkitchen.domain.mapper

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.model.CategoryItem
import com.google.firebase.database.DataSnapshot

class CategoryMapper{
    fun convertToDomain(categorySnapshot: DataSnapshot): List<CategoryGroup>{
        val categoryGroups = mutableListOf<CategoryGroup>()
        categorySnapshot.children.forEach {
            it.children.forEach{
                val groupSnapshot = it
                val categoryItems = mutableListOf<CategoryItem>()
                groupSnapshot.children.forEach{
                    val itemTitle: String = it.key.toString()
                    categoryItems.add(CategoryItem(itemTitle))
                }
                val groupTitle = groupSnapshot.key.toString()
                categoryGroups.add(CategoryGroup(groupTitle, categoryItems))
            }
        }
        //add the total item
        return categoryGroups
    }
}