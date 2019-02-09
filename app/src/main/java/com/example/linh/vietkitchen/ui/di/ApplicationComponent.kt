package com.example.linh.vietkitchen.ui.di

import android.app.Application
import com.example.linh.vietkitchen.admin.ui.screen.admin.AdminViewModel
import com.example.linh.vietkitchen.ui.di.module.DataSourceModule
import com.example.linh.vietkitchen.ui.di.module.FirebaseModule
import com.example.linh.vietkitchen.ui.screen.detailActivity.RecipeDetailViewModel
import com.example.linh.vietkitchen.ui.screen.home.favorite.FavoriteFragmentViewModel
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivityViewModel
import com.example.linh.vietkitchen.ui.screen.home.homeFragment.HomeFragmentViewModel
import com.example.linh.vietkitchen.ui.screen.home.profile.ProfileViewModel
import com.example.linh.vietkitchen.ui.screen.login.LoginViewModel
import com.example.linh.vietkitchen.ui.screen.searchScreen.SearchScreenViewModel
import com.example.linh.vietkitchen.ui.screen.splashScreen.SplashScreenViewModel
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [FirebaseModule::class, DataSourceModule::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    val homeActivityViewModel: HomeActivityViewModel
    val adminActivityViewModel: AdminViewModel

    val splashScreenFragmentViewModel: SplashScreenViewModel
    val loginFragmentViewModel: LoginViewModel
    val homeFragmentViewModel: HomeFragmentViewModel
    val searchScreenFragmentViewModel: SearchScreenViewModel
    val favoriteFragmentViewModel: FavoriteFragmentViewModel
    val profileFragmentViewModel: ProfileViewModel
    val detailFragmentViewModel: RecipeDetailViewModel
}
