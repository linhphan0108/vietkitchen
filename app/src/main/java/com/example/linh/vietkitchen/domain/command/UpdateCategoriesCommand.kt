package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.CategoryProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class UpdateCategoriesCommand @Inject constructor(private val provider: CategoryProvider)
    : CommandCoroutines<Boolean> {
    lateinit var listCatGroup: List<CategoryGroup>

    override fun execute(context: Context): LiveData<Resource<Boolean>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute() = provider.updateCategories(listCatGroup)
}