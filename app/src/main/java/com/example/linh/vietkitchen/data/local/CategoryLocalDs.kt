package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.google.firebase.database.DataSnapshot
import io.reactivex.Flowable

class CategoryLocalDs : CategoryDataSource{
    override fun getCategories(): Flowable<DataSnapshot>? {
        return null
    }

}