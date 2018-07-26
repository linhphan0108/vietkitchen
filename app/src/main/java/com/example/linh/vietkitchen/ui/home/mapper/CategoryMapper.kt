package com.example.linh.vietkitchen.ui.home.mapper

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem

class CategoryMapper {
    fun convertToUI(categories: List<CategoryGroup>): List<DrawerNavGroupItem> {
        return categories.map {
            var navChildren: List<DrawerNavChildItem> = listOf()
            if (it.itemsList.isNotEmpty()){
                navChildren = it.itemsList.map {
                    DrawerNavChildItem(it.itemTitle)
                }

            }
            val groupTitle = it.headerTile
            DrawerNavGroupItem(groupTitle, navChildren)
        }
    }
}