package com.example.linh.vietkitchen.util

import com.example.linh.vietkitchen.extension.attractUrlFromAnnotation
import com.example.linh.vietkitchen.ui.model.Recipe
import timber.log.Timber

object RecipeUtil{
    fun extractImagePaths(recipe: Recipe): List<String> {
        Timber.d("extract Image path from the recipe's content")
        val multiPartFiles = mutableListOf<String>()
        with(recipe){
            if (imageUrl.isNotBlank()) multiPartFiles.add(imageUrl)
            if (thumbUrl.isNotBlank()) multiPartFiles.add(thumbUrl)
            preparation.attractUrlFromAnnotation()?.forEachIndexed { _, s ->
                multiPartFiles.add(s)
            }
            processing.attractUrlFromAnnotation()?.forEachIndexed { _, s ->
                multiPartFiles.add(s)
            }
        }
        return multiPartFiles.toList()
    }
}