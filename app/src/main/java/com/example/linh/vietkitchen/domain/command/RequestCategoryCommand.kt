package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.CategoryProvider
import javax.inject.Inject

class RequestCategoryCommand @Inject constructor(private val provider: CategoryProvider)
    : CommandCoroutines<Response<List<CategoryGroup>>> {

    override suspend fun execute(context: Context): Response<List<CategoryGroup>> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute() = provider.getCategories()
}