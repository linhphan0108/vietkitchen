package com.example.linh.vietkitchen.di.module

import android.app.Application
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object AppModule {
    @JvmStatic @Provides @Singleton
    fun providesContext(app: Application): Application = app
}