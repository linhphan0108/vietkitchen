package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.domain.datasource.TagsDataSource
import com.google.firebase.database.DataSnapshot
import io.reactivex.Completable
import io.reactivex.Flowable

class TagsLocalDataSource : TagsDataSource {
    override fun getTags(): Flowable<DataSnapshot>? = null

    override fun putTags(tags: Map<String, Boolean>): Completable? = null
}