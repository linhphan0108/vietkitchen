package com.example.linh.vietkitchen.domain.provider

import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.data.cloud.CategoryCloudDs
import com.example.linh.vietkitchen.data.local.CategoryLocalDs
import com.example.linh.vietkitchen.data.response.ApiResponse
import com.example.linh.vietkitchen.domain.mapper.CategoryMapper
import com.example.linh.vietkitchen.domain.model.CategoryGroup
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class CategoryProvider @Inject constructor(
        private val mapper: CategoryMapper,
        private val localDataSource: CategoryLocalDs,
        private val cloudDataSource: CategoryCloudDs){

    fun getCategories(): LiveData<Resource<List<CategoryGroup>>>{
        return object : NetworkBoundResource<List<CategoryGroup>, List<CategoryGroup>>(){
            override fun saveCallResult(item: List<CategoryGroup>): List<CategoryGroup> {
                return item
            }

            override fun callDb(): LiveData<List<CategoryGroup>?> {
                return localDataSource.getCategories()
            }

            override fun createCall(): LiveData<ApiResponse<List<CategoryGroup>>> {
                return cloudDataSource.getCategories()
            }
        }.execute()
    }

    fun updateCategories(cats: List<CategoryGroup>) : LiveData<Resource<Boolean>> {
        val data = mapper.convertToData(cats)
        return object : PushNetworkResource<Boolean, Boolean>(){
            override fun saveCallResult(item: Boolean): Boolean {
                return item
            }

            override fun callDb(): LiveData<Boolean?> {
                return localDataSource.updateCategories(data)
            }

            override fun createCall(): LiveData<ApiResponse<Boolean>> {
                return cloudDataSource.updateCategories(data)
            }
        }.execute()
    }
}