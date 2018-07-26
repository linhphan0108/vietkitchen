package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.CategoryCloudDs
import com.example.linh.vietkitchen.data.local.CategoryLocalDs
import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import io.reactivex.Flowable

class CategoryProvider(sources: List<CategoryDataSource> = SOURCES) : BaseProvider<CategoryDataSource>(sources){
    companion object {
        val SOURCES by lazy {
            listOf(CategoryLocalDs(), CategoryCloudDs())
        }
    }

    fun getCategories(): Flowable<List<CategoryGroup>> = requestToSources {
        it.getCategories()
    }
}