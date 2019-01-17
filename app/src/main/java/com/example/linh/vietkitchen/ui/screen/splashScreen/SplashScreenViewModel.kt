package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.app.Application
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipesIdCommand
import com.example.linh.vietkitchen.exception.NoInternetConnection
import com.example.linh.vietkitchen.ui.VietKitchenApp.Companion.category
import com.example.linh.vietkitchen.ui.VietKitchenApp.Companion.userInfo
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.baseMVVM.StatusBox
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.*

class SplashScreenViewModel(application: Application,
        private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
        private val categoryMapper: CategoryMapper = CategoryMapper(),
        private val requestRecipesIdCommand: RequestRecipesIdCommand = RequestRecipesIdCommand())
    :  BaseViewModel(application){

    internal val silentLoginStatus: MutableLiveData<StatusBox<Boolean>> = MutableLiveData()
    internal val likedRecipesId: MutableLiveData<StatusBox<List<String>>> = MutableLiveData()
    internal var requestNavStatus: MutableLiveData<StatusBox<List<DrawerNavGroupItem>>> = MutableLiveData()
    internal val listNav: List<DrawerNavGroupItem>? = null

    fun checkLogin() {
        if (FirebaseAuth.getInstance().currentUser != null){
            silentLoginStatus.value = StatusBox(Status.HAS_LOGGED_IN, data = true)
        }else {
            silentLogin()
        }
    }

    private fun silentLogin() {
        Timber.d("login silently")
        val providers = getSelectedProviders()
        AuthUI.getInstance().silentSignIn(getApplication(), providers)
//                .continueWithTask {
//                    if (it.isSuccessful) {
//                        it
//                    } else {
//                        // Ignore any exceptions since we don't care about credential fetch errors.
//                        FirebaseAuth.getInstance().signInAnonymously()
//                    }
//                }
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        silentLoginStatus.value = StatusBox(Status.HAS_LOGGED_IN, data = true)
                        // Signed in! Start loading data
                    } else {
                        // Uh oh, show error data
                        Timber.e("log in failed ${it.exception?.message}")
                        silentLoginStatus.value = StatusBox(Status.HAS_LOGGED_IN, it.exception?.message, false)
                    }
                }
    }

    fun requestLikedRecipesId() {
        launchDataLoad({
            val recipesId = withIoContext {
                requestRecipesIdCommand.uid = userInfo.uid
                requestRecipesIdCommand.execute(getApplication())
            }
            userInfo.likedRecipesIds = recipesId.reversed().toMutableList()
            userInfo.numberFavoriteRecipes = recipesId.size
            likedRecipesId.value = StatusBox(Status.SUCCESS)
        }, {e->
            when(e){
                is NoInternetConnection ->{
                    likedRecipesId.value = StatusBox(Status.ERROR_NO_INTERNET)
                }
                else -> {
                    likedRecipesId.value = StatusBox(Status.ERROR, e.message)
                }
            }
        }, false)
    }

    fun requestCategory() {
        launchDataLoad({
            val categories = withIoContext {
                Timber.d("on launchDataLoad: ${Looper.myLooper() == Looper.getMainLooper()}")
                val response = requestCategoryCommand.execute()
                response.data?.let {listCategories->
                    return@withIoContext categoryMapper.convertToUI(listCategories)
                }
            }
            categories?.let {
                category.value = it
                requestNavStatus.value = StatusBox(Status.SUCCESS)
            }
        },{e->
            when(e){
                is NoInternetConnection ->{
                    requestNavStatus.value = StatusBox(Status.ERROR_NO_INTERNET)
                }
                else -> {
                    requestNavStatus.value = StatusBox(Status.ERROR, e.message)
                }
            }
            Timber.e(e)
        }, false)
    }

    fun getSelectedProviders(): List<AuthUI.IdpConfig> = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//                AuthUI.IdpConfig.FacebookBuilder().build()
//                AuthUI.IdpConfig.TwitterBuilder().build()
    )
}