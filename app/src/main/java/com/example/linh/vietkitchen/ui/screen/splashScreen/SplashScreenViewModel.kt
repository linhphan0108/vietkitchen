package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.domain.command.RequestRecipesIdCommand
import com.example.linh.vietkitchen.exception.NoInternetConnection
import com.example.linh.vietkitchen.ui.VietKitchenApp.Companion.userInfo
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.baseMVVM.StatusBox
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.*

class SplashScreenViewModel(application: Application,
                            private val requestRecipesIdCommand: RequestRecipesIdCommand = RequestRecipesIdCommand())
    :  BaseViewModel(application){

    val silentLoginStatus: MutableLiveData<StatusBox<Boolean>> = MutableLiveData()
    val likedRecipesId: MutableLiveData<StatusBox<List<String>>> = MutableLiveData()

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

    fun requestLikedRecipesId(uid: String) {
        launchDataLoad({
            val recipesId = withIoContext {
                requestRecipesIdCommand.uid = uid
                requestRecipesIdCommand.execute(getApplication())
            }
            userInfo.likedRecipesIds = recipesId.toMutableList()
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

    fun getSelectedProviders(): List<AuthUI.IdpConfig> = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//                AuthUI.IdpConfig.FacebookBuilder().build()
//                AuthUI.IdpConfig.TwitterBuilder().build()
    )
}