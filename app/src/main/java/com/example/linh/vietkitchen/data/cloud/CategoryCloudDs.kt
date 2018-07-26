package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.cloud.mapper.CategoryMapper
import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import timber.log.Timber

class CategoryCloudDs(private val mapper: CategoryMapper = CategoryMapper()) : CategoryDataSource {
    companion object {
        const val STORAGE_FOOD = "category"
    }

    private val database  by lazy { FirebaseDatabase.getInstance()}
    private val dbRef by lazy{ database.getReference(STORAGE_FOOD)}

    override fun getCategories(): Flowable<List<CategoryGroup>> {
        return RxFirebaseDatabase.observeValueEvent(dbRef){
            Timber.d("category's data's length ${it.children.count()}")
                mapper.convertToDomain(it)
        }
    }
}