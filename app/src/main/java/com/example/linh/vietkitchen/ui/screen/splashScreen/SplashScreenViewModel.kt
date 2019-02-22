package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.app.Application
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations.map
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipesIdCommand
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.vo.Resource
import com.example.linh.vietkitchen.vo.Status.*
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import timber.log.Timber
import java.util.*
import javax.inject.Inject

class SplashScreenViewModel @Inject constructor(application: Application,
        private val requestCategoryCommand: RequestCategoryCommand,
        private val categoryMapper: CategoryMapper,
        private val requestRecipesIdCommand: RequestRecipesIdCommand)
    :  BaseViewModel(application){

    private val _silentLoginStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    internal val silentLoginStatus: LiveData<Resource<Boolean>> = _silentLoginStatus

    fun checkLogin() {
        if (FirebaseAuth.getInstance().currentUser != null){
            _silentLoginStatus.value = Resource.success(true)
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
                        _silentLoginStatus.value = Resource.success(true)
                        // Signed in! Start loading data
                    } else {
                        // Uh oh, show error data
                        Timber.e("log in failed ${it.exception?.message}")
                        _silentLoginStatus.value = Resource.error(it.exception?.message)
                    }
                }
    }

    fun requestLikedRecipesId(): LiveData<Resource<Nothing>> {
            requestRecipesIdCommand.uid = VietKitchenApp.getUserInfo().uid
            val liveData = requestRecipesIdCommand.execute(getApplication())
            return map(liveData){ resource ->
                when(resource.status){
                    SUCCESS -> {
                        VietKitchenApp.getUserInfo().likedRecipesIds = resource.data!!.reversed().toMutableList()
                        VietKitchenApp.getUserInfo().numberFavoriteRecipes = resource.data.size
                        Resource.success(null)
                    }
                    LOADING -> {
                        Resource.loading()
                    }
                    ERROR -> {
                        Resource.error(resource.message)
                    }
                }
            }
    }

    fun requestCategory(): LiveData<Resource<List<DrawerNavGroupItem>>> {
            Timber.d("on launchDataLoad: ${Looper.myLooper() == Looper.getMainLooper()}")
            val response = requestCategoryCommand.execute(getApplication())
            return map(response){ resource ->
                when(resource.status){
                    SUCCESS -> {
                        val data = categoryMapper.convertToUI(resource.data!!)
                        VietKitchenApp.setCategory(data)
                        Resource.success(null)
                    }

                    LOADING -> {
                        Resource.loading()
                    }

                    ERROR -> {
                        Resource.error(resource.message)
                    }
                }
            }
    }

    fun getSelectedProviders(): List<AuthUI.IdpConfig> = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//                AuthUI.IdpConfig.FacebookBuilder().build()
//                AuthUI.IdpConfig.TwitterBuilder().build()
    )
}
