package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import io.reactivex.Flowable

class DeleteImagesCommand(private val recipeProvider: RecipeProvider = RecipeProvider())
    : CommandFollowable<Boolean> {

    override fun executeOnTheInternet(context: Context): Flowable<out Boolean> {
        return isInternetOn(context)
                .flatMap { execute() }
    }

    lateinit var fileUrls: List<String>

    override fun execute(): Flowable<out Boolean> {
        return recipeProvider.deleteImages(fileUrls)
    }
}