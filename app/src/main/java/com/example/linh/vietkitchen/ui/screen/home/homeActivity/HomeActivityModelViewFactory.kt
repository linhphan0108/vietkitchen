package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper

class HomeActivityModelViewFactory(val application: Application,
       private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
       private val categoryMapper: CategoryMapper = CategoryMapper())
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeActivityViewModel(application, requestCategoryCommand, categoryMapper) as T
    }
}