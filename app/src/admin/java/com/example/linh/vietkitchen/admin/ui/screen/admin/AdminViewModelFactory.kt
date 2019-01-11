package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand

class AdminViewModelFactory(private val application: Application,
        private val requestTagsCommand: RequestTagsCommand = RequestTagsCommand())
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return AdminViewModel(application, requestTagsCommand) as T
    }
}