package com.example.linh.vietkitchen.domain.mapper

import com.example.linh.vietkitchen.data.cloud.Recipe
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.extension.toMapOfStringBoolean
import com.google.firebase.database.DataSnapshot
import com.example.linh.vietkitchen.domain.model.Recipe as RecipeDomain

class RecipeMapper() {

    fun convertToDomain(children: Iterable<DataSnapshot>): List<RecipeDomain> {
        return if (children.none()){
            listOf()
        }else {
            children.map {
                convertToDomain(it)
            }
        }
    }

    fun convertToDomain(dataSnapshot: DataSnapshot): RecipeDomain {
        val f = dataSnapshot.getValue(Recipe::class.java)!!
        val id = dataSnapshot.key
//        val preProcess = f.preparation.map {
//            ProcessStep(it.step, it.imageUrl)
//        }
//        val process = f.processing.map {
//            ProcessStep(it.step, it.imageUrl)
//        }
        return RecipeDomain(id, f.name, f.intro, f.ingredient, f.spice,
                f.preparation, f.processing, f.notes, f.categories.toListOfStringOfKey(),
                f.tags, f.thumbUrl, f.imageUrl)
    }

    fun toData(domain: RecipeDomain): Recipe {
        with(domain){
            return Recipe(name, intro, ingredient, spice, preparation, processing, notes,
                    categories.toMapOfStringBoolean(), tags, thumbUrl, imageUrl)
        }
    }
}