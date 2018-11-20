package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.data.cloud.Category
import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.google.firebase.database.DataSnapshot
import io.reactivex.Completable
import io.reactivex.Flowable

class CategoryLocalDs : CategoryDataSource{
    override fun updateCategories(category: Category): Completable? {
        return null
    }

    override fun getCategories(): Flowable<DataSnapshot>? {
        return null
    }

}