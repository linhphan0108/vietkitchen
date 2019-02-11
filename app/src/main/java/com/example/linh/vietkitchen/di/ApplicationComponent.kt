package com.example.linh.vietkitchen.di

import android.app.Application
import com.example.linh.vietkitchen.admin.ui.screen.admin.AdminViewModel
import com.example.linh.vietkitchen.di.module.DataSourceModule
import com.example.linh.vietkitchen.di.module.FirebaseModule
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

/**
 * I like to consider that as the Dagger configuration entry point. It unfortunately doesn't look
 * simple (even though it doesn't look that bad either) and it doesn't get much better than that.
 */
@Singleton
@Component(modules = [FirebaseModule::class, DataSourceModule::class])
interface ApplicationComponent {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder
        fun build(): ApplicationComponent
    }

    /**
     * We could've chosen to create an inject() method instead and do field injection in the
     * Activity, but for this case this seems less verbose to me in the end.
     */
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
