package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import io.reactivex.Flowable

interface CategoryDataSource {
    fun getCategories(): Flowable<List<CategoryGroup>>?
}