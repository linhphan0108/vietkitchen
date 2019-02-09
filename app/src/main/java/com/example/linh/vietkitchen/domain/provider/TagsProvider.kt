package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.TagsCloudDataSource
import com.example.linh.vietkitchen.data.local.TagsLocalDataSource
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.TagsDataSource
import com.example.linh.vietkitchen.domain.mapper.TagsMapper
import javax.inject.Inject

class TagsProvider @Inject constructor(private val mapper: TagsMapper,
                                       localDataSource: TagsLocalDataSource, cloudDataSource: TagsCloudDataSource)
    : BaseProvider<TagsDataSource>(listOf(localDataSource, cloudDataSource)) {

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