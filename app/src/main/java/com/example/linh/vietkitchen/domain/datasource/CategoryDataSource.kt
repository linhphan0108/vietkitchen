package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.google.firebase.database.DataSnapshot
import io.reactivex.Flowable

interface CategoryDataSource {
    fun getCategories(): Flowable<DataSnapshot>?
}