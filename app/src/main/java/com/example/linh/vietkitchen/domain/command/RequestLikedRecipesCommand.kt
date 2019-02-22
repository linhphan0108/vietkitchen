package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class RequestLikedRecipesCommand @Inject constructor(private val provider: RecipeProvider)
    : CommandCoroutines<List<Recipe>> {
    var ids: List<String>? = null

    override fun execute(context: Context): LiveData<Resource<List<Recipe>>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute(): LiveData<Resource<List<Recipe>>> {
        return if (ids != null && ids!!.isNotEmpty()){
            provider.requestLikedRecipes(ids!!)
        }else {
            throw NullPointerException("ids must be not null or empty")
        }
    }
}