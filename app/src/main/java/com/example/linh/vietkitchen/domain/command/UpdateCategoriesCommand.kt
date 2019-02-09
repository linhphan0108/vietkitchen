package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.CategoryProvider
import javax.inject.Inject

class UpdateCategoriesCommand @Inject constructor(private val provider: CategoryProvider)
    : CommandCoroutines<Response<Boolean>> {
    lateinit var listCatGroup: List<CategoryGroup>

    override suspend fun execute(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<Boolean> {
        return provider.updateCategories(listCatGroup)
    }
}