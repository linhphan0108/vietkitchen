package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.Constants.PAGINATION_LENGTH
import io.reactivex.Completable
import io.reactivex.Flowable

interface FoodDataSource{
    fun getAllFood(tag: String? = null, limit: Int = PAGINATION_LENGTH, startAtId: String? = null) : Flowable<List<Food>>?
    fun putFoodWithDumpData(): Completable?
}