package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.CategoryCloudDs
import com.example.linh.vietkitchen.data.local.CategoryLocalDs
import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.example.linh.vietkitchen.domain.mapper.CategoryMapper
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import io.reactivex.Completable
import io.reactivex.Flowable
import timber.log.Timber

class CategoryProvider(private val mapper: CategoryMapper = CategoryMapper(),
                       sources: List<CategoryDataSource> = SOURCES) : BaseProvider<CategoryDataSource>(sources){
    companion object {
        val SOURCES by lazy {
            listOf(CategoryLocalDs(), CategoryCloudDs())
        }
    }

    fun getCategories(): Flowable<List<CategoryGroup>> = requestToSources {
        it.getCategories()?.map{dataSnapshot ->
            Timber.d("category's data's length ${dataSnapshot.children.count()}")
            mapper.convertToDomain(dataSnapshot)
        }
    }

    fun updateCategories(cats: List<CategoryGroup>) : Completable = requestToSources {
        it.updateCategories(mapper.convertToData(cats))
    }
}