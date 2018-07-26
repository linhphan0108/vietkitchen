package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.FoodCloudDataSource
import com.example.linh.vietkitchen.data.local.FoodLocalDataSource
import com.example.linh.vietkitchen.domain.datasource.FoodDataSource
import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.extension.firstResult
import io.reactivex.Flowable

class FoodProvider(sources: List<FoodDataSource> = SOURCES) : BaseProvider<FoodDataSource>(sources){
    companion object {
        val SOURCES by lazy { listOf(FoodLocalDataSource(), FoodCloudDataSource()) }
    }

    fun requestFoods() : Flowable<List<Food>> = requestToSources {
        val result = it.getAllFood()
        result
    }
}