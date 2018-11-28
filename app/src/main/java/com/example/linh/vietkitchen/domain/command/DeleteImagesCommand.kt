package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import io.reactivex.Flowable

class DeleteImagesCommand(private val recipeProvider: RecipeProvider = RecipeProvider())
    : CommandFollowable<Boolean> {

    lateinit var fileUrls: List<String>

    override fun execute(): Flowable<out Boolean> {
        return recipeProvider.deleteImages(fileUrls)
    }
}