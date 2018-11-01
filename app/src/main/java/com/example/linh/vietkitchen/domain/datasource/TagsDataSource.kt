package com.example.linh.vietkitchen.domain.datasource

import com.google.firebase.database.DataSnapshot
import io.reactivex.Completable
import io.reactivex.Flowable

interface TagsDataSource {
    fun getTags() : Flowable<DataSnapshot>?
    fun putTags(tags: Map<String, Boolean>) : Completable?
}