package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.TagsCloudDataSource
import com.example.linh.vietkitchen.data.local.TagsLocalDataSource
import com.example.linh.vietkitchen.domain.datasource.TagsDataSource
import com.example.linh.vietkitchen.domain.mapper.TagsMapper
import io.reactivex.Completable
import io.reactivex.Flowable

class TagsProvider(private val mapper: TagsMapper = TagsMapper(),
                   sources: List<TagsDataSource> = SOURCES) : BaseProvider<TagsDataSource>(sources) {
    companion object {
        val SOURCES by lazy {
            listOf(TagsLocalDataSource(), TagsCloudDataSource())
        }
    }

    fun getTags() : Flowable<Map<String, Boolean>> = requestToSources { it ->
        it.getTags()?.map { dataSnapshot ->
            mapper.convertToDomain(dataSnapshot)
        }
    }

    fun putTags(tags: Map<String, Boolean>) : Completable = requestToSources {
        it.putTags(tags)
    }
}