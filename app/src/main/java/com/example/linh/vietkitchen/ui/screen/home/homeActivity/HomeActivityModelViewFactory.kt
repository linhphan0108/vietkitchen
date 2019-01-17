package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class HomeActivityModelViewFactory(val application: Application)
    : ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return HomeActivityViewModel(application) as T
    }
}