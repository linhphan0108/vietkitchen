package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.domain.provider.CategoryProvider
import io.reactivex.Flowable

class RequestCategoryCommand(private val provider: CategoryProvider = CategoryProvider()) : CommandFollowable<List<CategoryGroup>> {

    override fun executeOnTheInternet(context: Context): Flowable<out List<CategoryGroup>> {
        return isInternetOn(context)
                .flatMap { execute() }
    }

    override fun execute() =  provider.getCategories()
}