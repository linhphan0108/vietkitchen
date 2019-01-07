package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.RecipeProvider

class DeleteImagesCommand(private val recipeProvider: RecipeProvider = RecipeProvider())
    : CommandCoroutines<Response<Boolean>> {

    override suspend fun execute(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }

    lateinit var fileUrls: List<String>

    override suspend fun execute(): Response<Boolean> {
        return recipeProvider.deleteImages(fileUrls)
    }
}