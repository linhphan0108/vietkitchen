package com.example.linh.vietkitchen.di

import android.app.Activity
import androidx.fragment.app.Fragment

/**
 * This exists so things get less coupled from the application class. Thanks to this interface,
 * our application class doesn't need to be open just so our test application class can extend it.
 */
interface DaggerComponentProvider {
    val component: ApplicationComponent
}

/**
 * And this exists to makes things beautiful in the Activity. Who needs `dagger-android`?
 */
val Activity.injector get() = (application as DaggerComponentProvider).component

val Fragment.injector get() = (activity?.application as DaggerComponentProvider).component