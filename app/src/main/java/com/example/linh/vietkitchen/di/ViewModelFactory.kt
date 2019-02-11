package com.example.linh.vietkitchen.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders


@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(crossinline factory: () -> T) = T::class.java.let {
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = factory() as T
    }).get(it)
}

/**
 * This is the only [ViewModel] factory we'll ever need to write as long as we stick with Dagger.
 *
 * You can use it to inject whatever `ViewModel` you want, and it won't recreate the `ViewModel`
 * dependencies on configuration change since the `ViewModel` dependency itself is [Lazy] here.
 *
 * Credits: https://gist.github.com/Elforama/969c2de0b3227f927fbf3f65654acf63
 */
inline fun <reified T : ViewModel> viewModel(activity: FragmentActivity, crossinline initializer: () -> T) =
    lazy(LazyThreadSafetyMode.NONE) { activity.getViewModel { initializer() } }

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.getViewModel(crossinline factory: () -> T) = T::class.java.let {
    ViewModelProviders.of(this, object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>) = factory() as T
    }).get(it)
}

inline fun <reified T : ViewModel> viewModel(fragment: Fragment, crossinline initializer: () -> T) =
        lazy(LazyThreadSafetyMode.NONE) { fragment.getViewModel { initializer() } }