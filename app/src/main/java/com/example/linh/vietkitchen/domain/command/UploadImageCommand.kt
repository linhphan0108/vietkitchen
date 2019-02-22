package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.data.cloud.ImageUpload
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.vo.Resource

class UploadImageCommand(private val provider: RecipeProvider)
    : CommandCoroutines<List<ImageUpload>> {
    lateinit var multiPartFileMap: List<ImageUpload>

    override fun execute(context: Context): LiveData<Resource<List<ImageUpload>>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute() = provider.uploadImages(multiPartFileMap)
}