package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.CategoryProvider

class RequestCategoryCommand(private val provider: CategoryProvider = CategoryProvider()) : CommandFollowable<List<CategoryGroup>> {
    override fun execute() =  provider.getCategories()
}