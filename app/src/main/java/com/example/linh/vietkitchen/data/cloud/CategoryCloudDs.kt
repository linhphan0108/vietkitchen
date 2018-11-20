package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.domain.datasource.CategoryDataSource
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers

class CategoryCloudDs : CategoryDataSource {
    companion object {
        const val STORAGE_FOOD = "category"
    }

    private val database  by lazy { FirebaseDatabase.getInstance()}
    private val dbRef by lazy{ database.getReference(STORAGE_FOOD)}

    override fun getCategories(): Flowable<DataSnapshot> {
        return RxFirebaseDatabase.observeValueEvent(dbRef)
                .observeOn(Schedulers.computation())
    }

    override fun updateCategories(category: Category): Completable? {
        return Completable.create { emitter ->
            dbRef.setValue(category.groups)
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }.addOnFailureListener {
                        emitter.onError(it)
                    }
        }.observeOn(Schedulers.computation())
    }
}