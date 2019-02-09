package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.CategoryCloudDs
import com.example.linh.vietkitchen.data.local.CategoryLocalDs
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.example.linh.vietkitchen.domain.mapper.CategoryMapper
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import javax.inject.Inject

class CategoryProvider @Inject constructor(private val mapper: CategoryMapper,
               localDataSource: CategoryLocalDs, cloudDataSource: CategoryCloudDs)
    : BaseProvider<CategoryDataSource>(listOf(localDataSource, cloudDataSource)){

    suspend fun getCategories(): Response<List<CategoryGroup>> = requestFirstSources {
        it.getCategories()?.let {response->
            mapper.convertToDomain(response)
        }
    }

    suspend fun updateCategories(cats: List<CategoryGroup>) : Response<Boolean> = requestFirstSources {
        it.updateCategories(mapper.convertToData(cats))
    }
}