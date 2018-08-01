package com.example.linh.vietkitchen.data.local

import com.example.linh.vietkitchen.domain.datasource.FoodDataSource
import com.example.linh.vietkitchen.domain.model.Food
import io.reactivex.Completable
import io.reactivex.Flowable

class FoodLocalDataSource : FoodDataSource {
    override fun putFoodWithDumpData(): Completable? = null

    override fun getAllFood(tag: String?, limit: Int, startAtId: String?): Flowable<List<Food>>? = null
}