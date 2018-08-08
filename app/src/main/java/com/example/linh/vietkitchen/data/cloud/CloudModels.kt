package com.example.linh.vietkitchen.data.cloud

data class CookingMethod(val name: String)
open class Ingredient(val name: String?, val quantity: Int, val unit: String, val note: String?){
    constructor(): this(null, 0, "", "")
    constructor(quantity: Int, unit: String): this(null, quantity, unit, "")
    constructor(quantity: Int, unit: String, note: String?): this(null, quantity, unit, note)
}
data class Season(val name:String)
data class Benefit(val name:String)
data class Region(val name: String)
data class SpecialDay(val name: String)
data class UserProfile(val userName: String, val name: String, val gender: Boolean, val age: Int)
data class Recipe(var id: String?=null, val name: String, val intro: String, val ingredient: Map<String, Ingredient>, val spice: String,
                  val preliminaryProcessing: List<String>, val processing: List<String>, val cookingMethod: Map<String, Boolean>,
                  val benefit: Map<String, Boolean>?, val recommendedSeason: Map<String, Boolean>, val region: String?, val specialDay: String?,
                  val tags: Map<String, Boolean>, val imageUrl: String){

    constructor(name: String, intro: String, ingredient: Map<String, Ingredient>, spice: String,
                preliminaryProcessing: List<String>, processing: List<String>, cookingMethod: Map<String, Boolean>,
                benefit: Map<String, Boolean>?,recommendedSeason: Map<String, Boolean>, region: String?, specialDay: String?,
                tags: Map<String, Boolean>, imageUrl: String) :
            this(null, name, intro, ingredient, spice, preliminaryProcessing, processing,
                    cookingMethod, benefit, recommendedSeason, region, specialDay, tags, imageUrl)

    constructor(): this(null, "", "", HashMap(), "", ArrayList(), ArrayList(), HashMap(),
            null, HashMap(), null, null, mapOf(), "")
}

data class Category(var groups: List<Map<String, List<Map<String, Boolean>>>>?){
    constructor(): this(null)
}

data class UserInfo(val favorites: Int, val schedules: Int)