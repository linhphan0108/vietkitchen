package com.example.linh.vietkitchen.ui.screen.splashScreenonActivityResult

import android.content.Intent
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface SplashScreenContractView : BaseViewContract{
    fun onHasLoggedIn()
    fun onHasNotLoggedIn()
    fun onRequestLikedRecipesIdSuccess(recipesId: List<String>)
    fun onRequestLikedRecipesIdFailed()
}

interface SplashScreenContractPresenter : BasePresenterContract<SplashScreenContractView>{
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    fun checkLogin()
    fun requestLikedRecipesId(uid: String)
}