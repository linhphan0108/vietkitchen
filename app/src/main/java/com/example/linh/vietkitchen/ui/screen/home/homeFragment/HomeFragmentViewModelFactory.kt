package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.linh.vietkitchen.domain.command.DeleteImagesCommand
import com.example.linh.vietkitchen.domain.command.DeleteRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.domain.command.UpdateCategoriesCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.RecipeMapper
import com.example.linh.vietkitchen.ui.model.UserInfo

class HomeFragmentViewModelFactory(private val application: Application,
                                   private val userInfo: UserInfo = VietKitchenApp.userInfo,
                                   private val recipeMapper: RecipeMapper = RecipeMapper(),
                                   private val categoryMapper: CategoryMapper = CategoryMapper(),
                                   private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                                   private val deleteRecipeCommand: DeleteRecipeCommand = DeleteRecipeCommand(),
                                   private val deleteImagesCommand: DeleteImagesCommand = DeleteImagesCommand(),
                                   private val updateCategoriesCommand: UpdateCategoriesCommand = UpdateCategoriesCommand()) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeFragmentViewModel(application, userInfo, recipeMapper, categoryMapper,
                requestRecipeCommand, deleteRecipeCommand, deleteImagesCommand, updateCategoriesCommand) as T
    }
}