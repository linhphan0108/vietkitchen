package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.provider.RecipeProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class DeleteImagesCommand @Inject constructor(private val recipeProvider: RecipeProvider)
    : CommandCoroutines<Boolean>{

    override fun execute(context: Context): LiveData<Resource<Boolean>> {
        isInternetOn(context)
        return execute()
    }

    lateinit var fileUrls: List<String>

    override fun execute(): LiveData<Resource<Boolean>> {
        return recipeProvider.deleteImages(fileUrls)
    }
}