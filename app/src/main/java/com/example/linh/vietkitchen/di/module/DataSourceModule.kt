package com.example.linh.vietkitchen.di.module

import com.example.linh.vietkitchen.data.cloud.CategoryCloudDs
import com.example.linh.vietkitchen.data.cloud.RecipeCloudDataSource
import com.example.linh.vietkitchen.data.cloud.TagsCloudDataSource
import com.example.linh.vietkitchen.data.cloud.UserCloudDataSource
import com.example.linh.vietkitchen.data.local.CategoryLocalDs
import com.example.linh.vietkitchen.data.local.RecipeLocalDataSource
import com.example.linh.vietkitchen.data.local.TagsLocalDataSource
import com.example.linh.vietkitchen.data.local.UserLocalDataSource
import com.example.linh.vietkitchen.domain.datasource.RecipeDataSource
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Named
import javax.inject.Singleton

@Module
object DataSourceModule {
//    @Singleton
//    @JvmStatic @Provides
//    fun provideRecipeCloudDataSources(database: FirebaseDatabase, storage: FirebaseStorage): RecipeCloudDataSource {
//        return RecipeCloudDataSource(database, storage)
//    }

    @Singleton
    @JvmStatic @Provides
    fun provideRecipeLocalDataSources(): RecipeLocalDataSource {
        return RecipeLocalDataSource()
    }

    @Singleton
    @JvmStatic @Provides
    fun provideTagLocalDataSources(): TagsLocalDataSource {
        return TagsLocalDataSource()
    }

//    @Singleton
//    @JvmStatic @Provides
//    fun provideTagCloudDataSources(database: FirebaseDatabase): TagsCloudDataSource {
//        return TagsCloudDataSource(database)
//    }

    @Singleton
    @JvmStatic @Provides
    fun provideCategoryLocalDataSources(): CategoryLocalDs {
        return CategoryLocalDs()
    }

//    @Singleton
//    @JvmStatic @Provides
//    fun provideCategoryCloudDataSources(database: FirebaseDatabase): CategoryCloudDs {
//        return CategoryCloudDs(database)
//    }

    @Singleton
    @JvmStatic @Provides
    fun provideUserLocalDataSources(): UserLocalDataSource {
        return UserLocalDataSource()
    }

    @Singleton
    @JvmStatic @Provides
    fun provideUserCloudDataSources(database: FirebaseDatabase): UserCloudDataSource {
        return UserCloudDataSource(database)
    }


}