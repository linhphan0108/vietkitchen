package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.data.response.PagingResponse
import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.extension.isNotNullAndNotBlank
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class RequestRecipeCommand @Inject constructor(private val provider: RecipeProvider)
    : CommandCoroutines<PagingResponse<List<Recipe>>>{
    var limit: Int = Constants.PAGINATION_LENGTH
    var title: String? = null
    var category: String? = null
    var tag: String? = null
    var startAtId: String? = null

    override fun execute(context: Context): LiveData<Resource<PagingResponse<List<Recipe>>>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute(): LiveData<Resource<PagingResponse<List<Recipe>>>> {
        return when {
            category.isNotNullAndNotBlank() -> provider.requestRecipeByCategory(cat = category, limit = limit, startAtId = startAtId)
            tag.isNotNullAndNotBlank() -> provider.requestRecipeByTag(tag = tag, limit = limit, startAtId = startAtId)
            else -> provider.requestRecipeByCategory(category, limit, startAtId)
        }
    }
}