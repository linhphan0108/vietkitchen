package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.CategoryCloudDs
import com.example.linh.vietkitchen.data.local.CategoryLocalDs
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.extension.CategoryDataSource
import com.example.linh.vietkitchen.domain.mapper.CategoryMapper
import com.example.linh.vietkitchen.domain.model.CategoryGroup

class CategoryProvider(private val mapper: CategoryMapper = CategoryMapper(),
                       sources: List<CategoryDataSource> = SOURCES) : BaseProvider<CategoryDataSource>(sources){
    companion object {
        val SOURCES by lazy {
            listOf(CategoryLocalDs(), CategoryCloudDs())
        }
    }

    suspend fun getCategories(): Response<List<CategoryGroup>> = requestFirstSources {
        it.getCategories()?.let {response->
            mapper.convertToDomain(response)
        }
    }

    suspend fun updateCategories(cats: List<CategoryGroup>) : Response<Boolean> = requestFirstSources {
        it.updateCategories(mapper.convertToData(cats))
    }
}