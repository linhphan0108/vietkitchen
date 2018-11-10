package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.content.Intent
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface SplashScreenContractView : BaseViewContract{
    fun onHasLoggedIn()
    fun onHasNotLoggedIn()
    fun onRequestLikedRecipesIdSuccess(recipesId: List<String>)
    fun onRequestLikedRecipesIdFailed()
    fun gotoHomeScreen()
}

interface SplashScreenContractPresenter : BasePresenterContract<SplashScreenContractView>{
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun checkLogin()
    fun gotoNextScreen()
    fun requestLikedRecipesId(uid: String)
}