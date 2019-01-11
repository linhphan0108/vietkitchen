package com.example.linh.vietkitchen.ui.screen.home.favorite

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.linh.vietkitchen.domain.command.RequestLikedRecipesCommand
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper

class FavoriteFragmentViewModelFactory(val application: Application,
       private val recipeMapper: RecipeMapper = RecipeMapper(),
       private val likedRecipesCommand: RequestLikedRecipesCommand = RequestLikedRecipesCommand()) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return FavoriteFragmentViewModel(application, recipeMapper, likedRecipesCommand) as T
    }
}