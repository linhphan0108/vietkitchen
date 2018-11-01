package com.example.linh.vietkitchen.ui.model

import android.os.Parcel
import android.os.Parcelable
import android.text.TextUtils
import com.example.linh.vietkitchen.domain.model.ProcessStep as DomainProcess


open class Ingredient(val name: String? = null, val notes: String?, val quantity: Int, val unit: String) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(notes)
        parcel.writeInt(quantity)
        parcel.writeString(unit)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Ingredient> {
        override fun createFromParcel(parcel: Parcel): Ingredient {
            return Ingredient(parcel)
        }

        override fun newArray(size: Int): Array<Ingredient?> {
            return arrayOfNulls(size)
        }
    }

    override fun toString(): String {
        return "$name $quantity$unit ($notes)"
    }
}

class Recipe(val id: String? = "", val name: String, val intro: String, val ingredient: Map<String, Ingredient>, val spice: String,
             var preparation: CharSequence, var processing: CharSequence, val cookingMethod: List<String>,
             val benefit: List<String>, val recommendedSeason: List<String>, val region: String?, val specialDay: String?,
             var tags: List<String>?, var thumbUrl: String, var imageUrl: String, var hasLiked: Boolean = false) : Entity(), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString(),//id
            parcel.readString(),//notes
            parcel.readString(),//intro
            readIngredient(parcel),//ingredient
            parcel.readString(),//spice
            TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel),//preparation
            TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(parcel),//processing
            readListString(parcel),//cookingMethod
            readListString(parcel),//benefit
            readListString(parcel),//recommendedSeason
            parcel.readString(),//region
            parcel.readString(),//specialDay
            readListString(parcel),//tags
            parcel.readString(),//thumbUrl
            parcel.readString(),//imageUrl
            parcel.readInt() != 0
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)//id
        parcel.writeString(name)//notes
        parcel.writeString(intro)//intro
        writeIngredient(ingredient, parcel, flags)//ingredient
        parcel.writeString(spice)////spice
        TextUtils.writeToParcel(preparation, parcel, flags)//preparation
        TextUtils.writeToParcel(processing, parcel, flags)//processing
        parcel.writeStringList(cookingMethod)//cookingMethod
        parcel.writeStringList(benefit)//benefit
        parcel.writeStringList(recommendedSeason)//recommendedSeason
        parcel.writeString(region)
        parcel.writeString(specialDay)
        parcel.writeStringList(tags)//tags
        parcel.writeString(thumbUrl)
        parcel.writeString(imageUrl)
        parcel.writeInt(if (hasLiked) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Recipe> {
        override fun createFromParcel(parcel: Parcel): Recipe {
            return Recipe(parcel)
        }

        override fun newArray(size: Int): Array<Recipe?> {
            return arrayOfNulls(size)
        }

        fun readIngredient(parcel: Parcel): Map<String, Ingredient> {
            val map = mutableMapOf<String, Ingredient>()
            val size = parcel.readInt()
            for (i in 0 until size){
                map[parcel.readString()] = parcel.readParcelable( Ingredient::class.java.classLoader)
            }
            return map
        }

        fun writeIngredient(map: Map<String, Ingredient>, parcel: Parcel, flags: Int){
            val size = map.size
            parcel.writeInt(size)
            if (size == 0) return
            for ((key, value) in map){
                parcel.writeString(key)
                parcel.writeParcelable(value, flags)
            }
        }

        fun readListString(parcel: Parcel): List<String>{
            var result: List<String> = mutableListOf()
            parcel.readStringList(result)
            return result
        }

        fun readMapStringBoolean(parcel: Parcel): MutableMap<String, Boolean> {
            val map = mutableMapOf<String, Boolean>()
            val size = parcel.readInt()
            for (i in 0 until size) {
                map[parcel.readString()] = parcel.readInt() != 0
            }
            return map
        }

        fun writeMapStringBoolean(map: Map<String, Boolean>?, parcel: Parcel, flags: Int){
            val size = map?.size ?: 0
            parcel.writeInt(size)
            if (size == 0) return
            for ((key, value) in map!!){
                parcel.writeString(key)
                parcel.writeInt(if (value) 1 else 0)
            }
        }

        fun writeMap(map: Map<String, String>, out: Parcel, flags: Int){
            out.writeInt(map.size)
            for ((key, value) in map){
                out.writeString(key)
                out.writeString(value)
            }
        }

        fun readMap(parcel: Parcel) : Map<String, String>{
            val map = mutableMapOf<String, String>()
            val size = parcel.readInt()
            for (i in 0 until  size) {
                map[parcel.readString()] = parcel.readString()
            }
            return map
        }

        fun readListProcess(parcel: Parcel): List<ProcessStep> {
            val result = mutableListOf<ProcessStep>()
            parcel.readTypedList(result, ProcessStep.CREATOR)
            return result.toList()
        }
    }

}

data class ProcessStep(val step: String = "", val imageUrl: String = "") : Parcelable{
    constructor(parcel: Parcel) : this(
            parcel.readString(),
            parcel.readString())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(step)
        parcel.writeString(imageUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProcessStep> {
        override fun createFromParcel(parcel: Parcel): ProcessStep {
            return ProcessStep(parcel)
        }

        override fun newArray(size: Int): Array<ProcessStep?> {
            return arrayOfNulls(size)
        }
    }

}
data class CategoryItem(val itemTitle: String)
data class CategoryGroup(val headerTile: String, val itemsList: List<CategoryItem>? = null)
