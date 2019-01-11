package com.example.linh.vietkitchen.ui.screen.searchScreen

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.mapper.TagMapper

class SearchScreenViewModelFactory(private val application: Application,
                                   private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                                   private val recipeMapper: RecipeMapper = RecipeMapper(),
                                   private val requestTagsCommand: RequestTagsCommand = RequestTagsCommand(),
                                   private val tagMapper: TagMapper = TagMapper(),
                                   private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                                   private val categoryMapper: CategoryMapper = CategoryMapper())
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SearchScreenViewModel(application, requestRecipeCommand, recipeMapper, requestTagsCommand,
                tagMapper, requestCategoryCommand, categoryMapper) as T
    }
}