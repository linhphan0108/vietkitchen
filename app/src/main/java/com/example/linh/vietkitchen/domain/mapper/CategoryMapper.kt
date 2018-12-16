package com.example.linh.vietkitchen.domain.mapper

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.data.cloud.Category as CatDataLayer
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.model.CategoryChild
import com.google.firebase.database.DataSnapshot

class CategoryMapper{
    fun convertToDomain(res: Response<DataSnapshot>): Response<List<CategoryGroup>> {
        val data = if (res.data != null) convertToDomain(res.data) else null
        return Response(res.code, data, res.message, res.exception)
    }
    fun convertToDomain(categorySnapshot: DataSnapshot): List<CategoryGroup>{
        val categoryGroups = mutableListOf<CategoryGroup>()
        categorySnapshot.children.forEachIndexed { order, orderDataSnapshot ->
            orderDataSnapshot.children.forEach{ groupDataSnapshot ->
                val categoryChildren = mutableListOf<CategoryChild>()
                val groupTitle = groupDataSnapshot.key.toString()
                val groupPath = "$order/$groupTitle"
                var numberItemsOfGroup = 0
                //extract the fist nav group
                //the all-one item
                if(groupDataSnapshot.children.count() == 0 && groupDataSnapshot.value is Long){
                    numberItemsOfGroup = groupDataSnapshot.getValue(Int::class.java)?: 0
                }else {
                    groupDataSnapshot.children.forEach { childDataSnapshot ->
                        val itemTitle: String = childDataSnapshot.key.toString()
                        val path = "$groupPath/$itemTitle"
                        val numberItems: Int = childDataSnapshot.getValue(Int::class.java) ?: 0
                        numberItemsOfGroup += numberItems
                        categoryChildren.add(CategoryChild(itemTitle, path, numberItems))
                    }
                }
                categoryGroups.add(CategoryGroup(groupTitle, groupPath, numberItemsOfGroup, categoryChildren))
            }
        }
        //add the total item
        return categoryGroups
    }

    fun convertToData(listCatGroup: List<CategoryGroup>): CatDataLayer{
        val groups: MutableList<Map<String, Any>> = mutableListOf()
        listCatGroup.forEach {groupItem ->
            val key = groupItem.headerTile
            val children: MutableMap<String, Int> = mutableMapOf()
            groupItem.itemsList?.forEach { childItem ->
                val k = childItem.itemTitle
                val value = childItem.numberItems
                children[k] = value
            }
            val mapGroup = mapOf(Pair(key, if (children.isEmpty()) groupItem.numberItems else children))
            groups.add(mapGroup)
        }

        return CatDataLayer(groups)
    }
}