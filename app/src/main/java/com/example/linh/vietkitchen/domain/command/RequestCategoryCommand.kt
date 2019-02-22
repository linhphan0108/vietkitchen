package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.CategoryProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class RequestCategoryCommand @Inject constructor(private val provider: CategoryProvider)
    : CommandCoroutines<List<CategoryGroup>> {

    override fun execute(context: Context): LiveData<Resource<List<CategoryGroup>>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute() = provider.getCategories()
}