package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.domain.provider.FoodProvider
import io.reactivex.Flowable

class RequestFoodCommand(private val provider: FoodProvider = FoodProvider()) : Command<List<Food>>{
    override fun execute(): Flowable<List<Food>> = provider.requestFoods()

}