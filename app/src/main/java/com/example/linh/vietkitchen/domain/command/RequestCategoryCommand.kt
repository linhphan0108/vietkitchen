package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.BaseProvider
import com.example.linh.vietkitchen.domain.provider.CategoryProvider
import io.reactivex.Flowable

class RequestCategoryCommand(private val provider: CategoryProvider = CategoryProvider()) : Command<List<CategoryGroup>> {
    override fun execute() =  provider.getCategories()
}