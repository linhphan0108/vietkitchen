package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.cloud.mapper.FoodMapper
import com.example.linh.vietkitchen.domain.datasource.FoodDataSource
import com.google.firebase.database.*
import com.example.linh.vietkitchen.domain.model.Food as FoodDomain
import durdinapps.rxfirebase2.RxFirebaseDatabase
import io.reactivex.Flowable
import timber.log.Timber

class FoodCloudDataSource(private val mapper: FoodMapper = FoodMapper()) : FoodDataSource {
    companion object {
        const val STORAGE_FOOD = "foods"
    }

    private val database  by lazy { FirebaseDatabase.getInstance()}
    private val dbRef by lazy{ database.getReference(STORAGE_FOOD)}

    override fun getAllFood(): Flowable<List<FoodDomain>>? {
        return RxFirebaseDatabase.observeValueEvent(dbRef){
            Timber.d("onFetchData data's length ${it.children.count()}")
            mapper.convertToDomain(it.children)
        }
    }

    //an example how to use an Rxjava  DisposableObserver natively
    //for retrieving data from firebase server
//    fun <T> getObservable(query: Query, clazz: Class<T>): Observable<T>? {
//        return Observable.create { emitter ->
//            query.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(dataSnapshot: DataSnapshot) {
//                    Timber.d(dataSnapshot.toString())
//                    val value = dataSnapshot.getValue(clazz)
//                    if (value != null) {
//                        if (!emitter.isDisposed) {
//                            emitter.onNext(value)
//                        }
//                    } else {
//                        query.removeEventListener(this)
//                        if (!emitter.isDisposed) {
//                            emitter.onError(FirebaseRxDataCastException("Unable to cast Firebase data response to " + clazz.simpleName))
//                        }
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    query.removeEventListener(this)
//                    if (!emitter.isDisposed) {
//                        emitter.onError(FirebaseRxDataException(error))
//                    }
//                }
//            })
//        }
//    }
}