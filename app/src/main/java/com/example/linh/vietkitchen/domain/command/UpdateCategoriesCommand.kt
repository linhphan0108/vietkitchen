package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.CategoryProvider
import io.reactivex.Completable

class UpdateCategoriesCommand(val provider: CategoryProvider = CategoryProvider()): CommandCompletable {
    lateinit var listCatGroup: List<CategoryGroup>
    override fun execute(): Completable {
        return provider.updateCategories(listCatGroup)
    }
}