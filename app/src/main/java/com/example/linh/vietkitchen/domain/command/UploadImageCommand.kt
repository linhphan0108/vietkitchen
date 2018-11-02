package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import io.reactivex.Flowable

class UploadImageCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandFollowable<ImageUpload> {
    lateinit var multiPartFileMap: List<ImageUpload>
    override fun execute(): Flowable<ImageUpload> {
        return provider.uploadImages(multiPartFileMap)
    }
}