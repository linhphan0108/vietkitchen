package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.domain.datasource.FoodDataSource
import com.example.linh.vietkitchen.domain.model.Food
import io.reactivex.Flowable

class FoodLocalDataSource : FoodDataSource {
    override fun getAllFood(): Flowable<List<Food>>? = null
}