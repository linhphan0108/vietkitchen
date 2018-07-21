package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.data.cloud.FoodCloudDataSource
import com.example.linh.vietkitchen.data.local.FoodLocalDataSource
import com.example.linh.vietkitchen.domain.datasource.FoodDataSource
import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.extension.firstResult
import io.reactivex.Flowable

class FoodProvider(private val sources: List<FoodDataSource> = SOURCES){
    companion object {
        val SOURCES by lazy { listOf(FoodLocalDataSource(), FoodCloudDataSource()) }
    }

    fun requestFoods() : Flowable<List<Food>> = requestToSources {
        val result = it.getAllFood()
        result
    }

    private fun <T : Any> requestToSources(f: (FoodDataSource) -> T?): T = sources.firstResult { f(it) }
}