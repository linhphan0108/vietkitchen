package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.domain.datasource.TagsDataSource
import com.example.linh.vietkitchen.exception.FirebaseDataException
import com.example.linh.vietkitchen.util.Constants
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.schedulers.Schedulers
import java.lang.Exception

class TagsCloudDataSource : TagsDataSource{
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val dbRefRecipe by lazy { database.getReference(Constants.STORAGE_RECIPES_TAGS_PATH) }

    override fun getTags(): Flowable<DataSnapshot> {
        return Flowable.create(FlowableOnSubscribe<DataSnapshot> { emitter ->
            dbRefRecipe.addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {
                    if(!emitter.isCancelled) {
                        emitter.onError(FirebaseDataException(p0))
                        emitter.onComplete()
                    }
                }

                override fun onDataChange(p0: DataSnapshot) {
                    if(!emitter.isCancelled) {
                        emitter.onNext(p0)
                        emitter.onComplete()
                    }
                }

            })
        }, BackpressureStrategy.DROP)
                .observeOn(Schedulers.computation())
    }

    override fun putTags(tags: Map<String, Boolean>) : Completable{
        return Completable.create { emitter ->
            var counter = 0
            var success = true
            tags.forEach {tag ->
                dbRefRecipe.child(tag.key).setValue(tag.value)
                        .addOnCompleteListener {
                            counter++
                            if (counter == tags.size && success){
                                emitter.onComplete()
                            }else if(counter == tags.size && !success){
                                emitter.onError(Exception())
                            }
                        }.addOnFailureListener {
                            success = false
                            if (++counter == tags.size) emitter.onError(it)
                        }
            }

        }.observeOn(Schedulers.computation())
    }
}