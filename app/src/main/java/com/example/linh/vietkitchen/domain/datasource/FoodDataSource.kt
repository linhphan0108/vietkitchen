package com.example.linh.vietkitchen.domain.datasource

import com.example.linh.vietkitchen.domain.model.Food
import io.reactivex.Flowable

interface FoodDataSource{
    fun getAllFood() : Flowable<List<Food>>?
}