package com.example.linh.vietkitchen.ui.di.module

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
object FirebaseModule {
    @JvmStatic @Provides @Singleton
    fun provideFireBaseDatabase(): FirebaseDatabase {
        return FirebaseDatabase.getInstance()
    }

    @JvmStatic @Provides @Singleton
    fun provideFireBaseStorage(): FirebaseStorage {
        return FirebaseStorage.getInstance()
    }
}