package com.example.linh.vietkitchen.domain.command

import android.net.Uri
import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource.MessageUploadCommunication
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import io.reactivex.Flowable

class UploadImageCommand(private val provider: RecipeProvider = RecipeProvider()) : CommandFollowable<MessageUploadCommunication> {
    lateinit var multiPartFileMap: Map<String, Uri>
    override fun execute(): Flowable<out MessageUploadCommunication> {
        return provider.uploadImages(multiPartFileMap)
    }
}