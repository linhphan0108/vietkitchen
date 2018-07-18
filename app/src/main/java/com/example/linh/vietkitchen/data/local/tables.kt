package com.example.linh.vietkitchen.data.local

data class CookingMethod(val name: String)
open class Ingredient(val name: String?, val quantity: Int, val unit: String){
    constructor(): this(null, 0, "")
    constructor(quantity: Int, unit: String): this(null, quantity, unit)
}
data class Season(val name:String)
data class Benefit(val name:String)
data class Region(val name: String)
data class SpecialDay(val name: String)
data class UserProfile(val userName: String, val name: String, val gender: Boolean, val age: Int)
data class Food(var id: String?=null, val name: String, val intro: String, val ingredient: Map<String, Ingredient>, val spice: String,
                val preliminaryProcessing: List<String>, val processing: List<String>, val cookingMethod: Map<String, Boolean>,
                val benefit: Map<String, Boolean>?,val recommendedSeason: Map<String, Boolean>, val region: String?, val specialDay: String?,
                val imageUrl: String){

    constructor(name: String, intro: String, ingredient: Map<String, Ingredient>, spice: String,
                preliminaryProcessing: List<String>, processing: List<String>, cookingMethod: Map<String, Boolean>,
                benefit: Map<String, Boolean>?,recommendedSeason: Map<String, Boolean>, region: String?, specialDay: String?,
                imageUrl: String) :
            this(null, name, intro, ingredient, spice, preliminaryProcessing, processing,
                    cookingMethod, benefit, recommendedSeason, region, specialDay, imageUrl)

    constructor(): this(null, "", "", HashMap(), "", ArrayList(), ArrayList(), HashMap(),
            null, HashMap(), null, null, "")
}