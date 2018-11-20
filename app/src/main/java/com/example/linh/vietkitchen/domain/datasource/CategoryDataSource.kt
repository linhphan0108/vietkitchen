package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.data.cloud.Category
import com.google.firebase.database.DataSnapshot
import io.reactivex.Completable
import io.reactivex.Flowable

interface CategoryDataSource {
    fun getCategories(): Flowable<DataSnapshot>?
    fun updateCategories(category: Category): Completable?
}