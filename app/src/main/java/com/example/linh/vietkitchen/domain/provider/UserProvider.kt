package com.example.linh.vietkitchen.domain.provider

import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.data.cloud.UserCloudDataSource
import com.example.linh.vietkitchen.data.local.UserLocalDataSource
import com.example.linh.vietkitchen.data.response.ApiResponse
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class UserProvider @Inject constructor
(private val localDataSource: UserLocalDataSource,
 private val cloudDataSource: UserCloudDataSource) {

    fun likeRecipe(uid: String, recipeKey: String): LiveData<Resource<String>> {
        return object : PushNetworkResource<String, String>(){
            override fun saveCallResult(item: String) : String{
                return item
            }

            override fun callDb(): LiveData<String> {
                return localDataSource.likeRecipe(uid, recipeKey)
            }

            override fun createCall(): LiveData<ApiResponse<String>> {
                return cloudDataSource.likeRecipe(uid, recipeKey)
            }

        }.execute()
    }

    fun unLikeRecipe(uid: String, recipeKey: String): LiveData<Resource<Boolean>> {
        return object : PushNetworkResource<Boolean, Boolean>(){
            override fun saveCallResult(item: Boolean) : Boolean{
                return item
            }

            override fun callDb(): LiveData<Boolean> {
                return localDataSource.unLikeRecipe(uid, recipeKey)
            }

            override fun createCall(): LiveData<ApiResponse<Boolean>> {
                return cloudDataSource.unLikeRecipe(uid, recipeKey)
            }
        }.execute()
    }

    fun requestLikedRecipesId(uid: String): LiveData<Resource<List<String>>> {
        return object : NetworkBoundResource<List<String>, List<String>>() {
            override fun saveCallResult(item: List<String>) : List<String>{
                return item
            }

            override fun callDb(): LiveData<List<String>> {
                return localDataSource.getLikedRecipesId(uid)
            }

            override fun createCall(): LiveData<ApiResponse<List<String>>> {
                return cloudDataSource.getLikedRecipesId(uid)
            }
        }.execute()
    }
}