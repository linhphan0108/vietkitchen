package com.example.linh.vietkitchen.data.cloud

data class CookingMethod(val name: String)
open class Ingredient(val quantity: Int, val unit: String, val note: String?){
    constructor(): this(0, "", "")
    constructor(quantity: Int, unit: String): this(quantity, unit, "")
}
data class Season(val name:String)
data class Benefit(val name:String)
data class Region(val name: String)
data class SpecialDay(val name: String)
data class ProcessStep(val step: String = "", val imageUrl: String = "")

data class Recipe(var id: String?=null, val name: String, val intro: String, val ingredient: Map<String, Ingredient>, val spice: String,
                  val preparation: String, val processing: String, val cookingMethod: Map<String, Boolean>,
                  val benefit: Map<String, Boolean>?, val recommendedSeason: Map<String, Boolean>, val region: String?, val specialDay: String?,
                  val tags: Map<String, Boolean>, val thumbUrl: String, val imageUrl: String){

    constructor(name: String, intro: String, ingredient: Map<String, Ingredient>, spice: String,
                preparation: String, processing: String, cookingMethod: Map<String, Boolean>,
                benefit: Map<String, Boolean>?, recommendedSeason: Map<String, Boolean>, region: String?, specialDay: String?,
                tags: Map<String, Boolean>, thumbUrl: String, imageUrl: String) :
            this(null, name, intro, ingredient, spice, preparation, processing,
                    cookingMethod, benefit, recommendedSeason, region, specialDay, tags, thumbUrl, imageUrl)

    constructor(): this(null, "", "", HashMap(), "", "", "", HashMap(),
            null, HashMap(), null, null, mapOf(), "", "")
}

data class Category(var groups: List<Map<String, List<Map<String, Boolean>>>>?){
    constructor(): this(null)
}

data class UserInfo(val favorites: Int, val schedules: Int)
data class UserProfile(val userName: String, val name: String, val gender: Boolean, val age: Int)
data class ImageUpload(var fileName: String, var originalPath: String, var optimizedPath: String, val progress: Int = 0, var remotePath: String? = null)