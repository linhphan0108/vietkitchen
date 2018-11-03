package com.example.linh.vietkitchen.domain.mapper

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.model.CategoryChild
import com.google.firebase.database.DataSnapshot

class CategoryMapper{
    fun convertToDomain(categorySnapshot: DataSnapshot): List<CategoryGroup>{
        val categoryGroups = mutableListOf<CategoryGroup>()
        categorySnapshot.children.forEachIndexed { order, orderDataSnapshot ->
            orderDataSnapshot.children.forEach{ groupDataSnapshot ->
                val categoryChildren = mutableListOf<CategoryChild>()
                val groupTitle = groupDataSnapshot.key.toString()
                val groupPath = "$order/$groupTitle"
                var numberItemsOfGroup = 0
                groupDataSnapshot.children.forEach{ childDataSnapshot ->
                    val itemTitle: String = childDataSnapshot.key.toString()
                    val path = "$groupPath/$itemTitle"
                    val numberItems: Int = childDataSnapshot.getValue(Int::class.java) ?: 0
                    numberItemsOfGroup += numberItems
                    categoryChildren.add(CategoryChild(itemTitle, path, numberItems))
                }
                categoryGroups.add(CategoryGroup(groupTitle, groupPath, numberItemsOfGroup, categoryChildren))
            }
        }
        //add the total item
        return categoryGroups
    }
}