package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.domain.provider.FoodProvider
import com.example.linh.vietkitchen.util.Constants
import io.reactivex.Flowable

class RequestFoodCommand(var category: String? = null, var limit: Int = Constants.PAGINATION_LENGTH,
                         var startAtId: String? = null,
                         private val provider: FoodProvider = FoodProvider())
    : CommandFollowable<List<Food>>{

    override fun execute(): Flowable<out List<Food>> {
        return provider.requestFoods(category, limit, startAtId)
    }
}