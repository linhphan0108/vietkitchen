package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.RecipeProvider

class UploadImageCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandCoroutines<Response<List<ImageUpload>>> {
    lateinit var multiPartFileMap: List<ImageUpload>

    override suspend fun execute(context: Context): Response<List<ImageUpload>> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<List<ImageUpload>> {
        return provider.uploadImages(multiPartFileMap)
    }
}