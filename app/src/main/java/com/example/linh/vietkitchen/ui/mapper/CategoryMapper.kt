package com.example.linh.vietkitchen.ui.mapper

import com.example.linh.vietkitchen.domain.model.CategoryChild
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.ui.model.DrawerNavChildItem
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem

class CategoryMapper {
    fun convertToUI(categories: List<CategoryGroup>): List<DrawerNavGroupItem> {
        return categories.map {
            var navChildren: List<DrawerNavChildItem>?  = null
            if (it.itemsList != null && it.itemsList.isNotEmpty()){
                navChildren = it.itemsList.map {
                    DrawerNavChildItem(it.itemTitle, it.path, it.numberItems)
                }

            }
            val groupTitle = it.headerTile
            DrawerNavGroupItem(groupTitle, it.path, it.numberItems, navChildren)
        }
    }

    fun toDomain(list: List<DrawerNavGroupItem>): List<CategoryGroup>{
        return list.map {navGroup ->
             val listChildren = navGroup.itemsList?.map {navChild ->
                 CategoryChild(navChild.itemTitle, navChild.path, navChild.numberItems)
             }
            CategoryGroup(navGroup.headerTile, navGroup.path, navGroup.numberItems, listChildren)
        }
    }
}