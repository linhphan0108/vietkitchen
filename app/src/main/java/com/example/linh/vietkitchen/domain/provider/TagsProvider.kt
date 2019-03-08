package com.example.linh.vietkitchen.domain.provider

import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.data.cloud.TagsCloudDataSource
import com.example.linh.vietkitchen.data.local.TagsLocalDataSource
import com.example.linh.vietkitchen.data.response.ApiResponse
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class TagsProvider @Inject constructor(
        private val localDataSource: TagsLocalDataSource,
        private val cloudDataSource: TagsCloudDataSource){

    fun getTags() : LiveData<Resource<Map<String, Boolean>>> {
        return object : NetworkBoundResource<Map<String, Boolean>, Map<String, Boolean>>(){
            override fun saveCallResult(item: Map<String, Boolean>): Map<String, Boolean> {
                return item
            }

            override fun callDb(): LiveData<Map<String, Boolean>?> {
                return localDataSource.getTags()
            }

            override fun createCall(): LiveData<ApiResponse<Map<String, Boolean>>> {
                return cloudDataSource.getTags()
            }
        }.execute()
    }

    fun putTags(tags: Map<String, Boolean>) : LiveData<Resource<Boolean>>{
        return object : PushNetworkResource<Boolean, Boolean>(){
            override fun saveCallResult(item: Boolean): Boolean {
                return item
            }

            override fun callDb(): LiveData<Boolean?> {
                return localDataSource.putTags(tags)
            }

            override fun createCall(): LiveData<ApiResponse<Boolean>> {
                return cloudDataSource.putTags(tags)
            }
        }.execute()
    }
}