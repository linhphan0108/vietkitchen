package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.TagsCloudDataSource
import com.example.linh.vietkitchen.data.local.TagsLocalDataSource
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.TagsDataSource
import com.example.linh.vietkitchen.domain.mapper.TagsMapper

class TagsProvider(private val mapper: TagsMapper = TagsMapper(),
                   sources: List<TagsDataSource> = SOURCES) : BaseProvider<TagsDataSource>(sources) {
    companion object {
        val SOURCES by lazy {
            listOf(TagsLocalDataSource(), TagsCloudDataSource())
        }
    }

    suspend fun getTags() : Response<Map<String, Boolean>> = requestFirstSources { it ->
        val dataResponse = it.getTags()
        dataResponse?.let {
            mapper.convertToDomain(dataResponse)
        }
    }

    suspend fun putTags(tags: Map<String, Boolean>) : Response<Boolean> = requestAllSources {
        it.putTags(tags)
    }
}