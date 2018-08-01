package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.provider.FoodProvider

class PutRecipeCommand(private val provider: FoodProvider = FoodProvider()) : CommandCompletable{
    override fun execute() = provider.putFood()

}