package com.example.linh.vietkitchen.ui.screen.detailActivity

import android.app.Application
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.example.linh.vietkitchen.domain.command.PutLikeCommand
import com.example.linh.vietkitchen.domain.command.PutUnlikeCommand

class DetailViewModelFactory(private val applicationContext: Application,
                             private val likeCommand: PutLikeCommand = PutLikeCommand(),
                             private val unlikeCommand: PutUnlikeCommand = PutUnlikeCommand()) : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return RecipeDetailViewModel(applicationContext, likeCommand, unlikeCommand) as T
    }
}