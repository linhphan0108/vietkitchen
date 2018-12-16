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

data class Recipe(var id: String?=null, val name: String, val intro: String?, val ingredient: String, val spice: String,
                  val preparation: String, val processing: String, val notes: String?, val categories: Map<String, Boolean>,
                  val tags: Map<String, Boolean>, val thumbUrl: String, val imageUrl: String){

    constructor(name: String, intro: String?, ingredient: String, spice: String,
                preparation: String, processing: String, notes: String?, categories: Map<String, Boolean>,
                tags: Map<String, Boolean>, thumbUrl: String, imageUrl: String) :
            this(null, name, intro, ingredient, spice, preparation, processing, notes,
                    categories, tags, thumbUrl, imageUrl)

    constructor(): this(null, "", "", "",
            "", "", "", null, mapOf<String, Boolean>(),
            mapOf<String, Boolean>(), "", "")
}

data class Category(var groups: List<Map<String, Any>>?){
    constructor(): this(null)
}

data class UserInfo(val favorites: Int, val schedules: Int)
data class UserProfile(val userName: String, val name: String, val gender: Boolean, val age: Int)
data class ImageUpload(var fileName: String, var originalPath: String, var optimizedPath: String, var progress: Int = 0, var remotePath: String? = null)