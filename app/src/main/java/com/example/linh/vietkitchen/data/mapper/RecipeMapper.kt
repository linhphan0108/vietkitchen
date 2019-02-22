package com.example.linh.vietkitchen.data.mapper

import com.example.linh.vietkitchen.data.cloud.Recipe
import com.google.firebase.database.DataSnapshot
import javax.inject.Inject

class RecipeMapper @Inject constructor() {

    fun toData(children: Iterable<DataSnapshot>): List<Recipe> {
        return if (children.none()){
            listOf()
        }else {
            children.map {
                toData(it)
            }
        }
    }

    fun toData(dataSnapshot: DataSnapshot): Recipe {
        return dataSnapshot.getValue(Recipe::class.java)!!.apply {
            id = dataSnapshot.key
        }
    }
}