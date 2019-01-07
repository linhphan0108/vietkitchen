package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.linh.vietkitchen.domain.command.RequestRecipesIdCommand

class SplashScreenViewModelFactory(val application: Application,
       private val requestRecipesIdCommand: RequestRecipesIdCommand = RequestRecipesIdCommand()): ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return SplashScreenViewModel(application, requestRecipesIdCommand) as T
    }
}