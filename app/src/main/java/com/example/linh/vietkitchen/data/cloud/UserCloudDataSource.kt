package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.domain.datasource.UserDataSource
import com.example.linh.vietkitchen.exception.FirebaseDataException
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_LIKED_RECIPES_PATH
import com.example.linh.vietkitchen.util.Constants.STORAGE_USER_PATH
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers

class UserCloudDataSource : UserDataSource{

    private val dbRef by lazy{
        FirebaseDatabase.getInstance().getReference(STORAGE_USER_PATH)}

    override fun likeRecipe(uid: String, recipeKey: String): Completable {
        return Completable.create { emitter ->
            val newDbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH).child(recipeKey)
            newDbRef.setValue(true)
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }.addOnFailureListener {
                        emitter.onError(it)
                    }
        }.observeOn(Schedulers.computation())

    }

    override fun unLikeRecipe(uid: String, recipeKey: String): Completable {
        return Completable.create { emitter ->
            val newDbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH).child(recipeKey)
            newDbRef.removeValue()
                    .addOnCompleteListener {
                        emitter.onComplete()
                    }.addOnFailureListener {
                        emitter.onError(it)
                    }
        }.observeOn(Schedulers.computation())

    }

    override fun getLikedRecipesId(uid: String): Flowable<DataSnapshot> {
        return Flowable.create(FlowableOnSubscribe<DataSnapshot> { emitter ->
            val dbRef = dbRef.child(uid).child(STORAGE_USER_LIKED_RECIPES_PATH)
            dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    emitter.onError(FirebaseDataException(p0))
                }

                override fun onDataChange(p0: DataSnapshot) {
                    emitter.onNext(p0)
                }
            })
        }, BackpressureStrategy.DROP)
                .observeOn(Schedulers.computation())

    }
}