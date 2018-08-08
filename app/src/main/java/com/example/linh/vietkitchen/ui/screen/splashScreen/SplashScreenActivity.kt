package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.VietKitchenApp
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import com.example.linh.vietkitchen.ui.model.UserInfo
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity
import com.example.linh.vietkitchen.ui.screen.splashScreenonActivityResult.SplashScreenContractPresenter
import com.example.linh.vietkitchen.ui.screen.splashScreenonActivityResult.SplashScreenContractView
import io.reactivex.internal.operators.flowable.FlowableEmpty
import kotlinx.android.synthetic.main.activity_splash_screen.*
import java.util.concurrent.TimeUnit


class SplashScreenActivity : BaseActivity<SplashScreenContractView, SplashScreenContractPresenter>(),
        SplashScreenContractView {
    companion object {
        private const val TIME_WAITING_IN_SPLASH_SCREEN = 7000//in millisecond
    }

    lateinit var userInfo: UserInfo
    var timeStartedSplashScreen: Long = 0

    //region lifecycle callbacks ===================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        timeStartedSplashScreen = System.currentTimeMillis()
        slackLoadingView.start()
        txtAppNameVersion.text = getString(R.string.app_name_version, BuildConfig.VERSION_NAME)
        presenter.checkLogin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        presenter.onActivityResult(requestCode, resultCode, data)
    }

    //endregion lifecycle callbacks

    //region MVP callbacks =========================================================================
    override val viewContext: Context?
        get() = this

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun initPresenter() = SplashScreenPresenter()

    override fun getViewContract() = this

    override fun getActivityLayoutRes() = R.layout.activity_splash_screen

    override fun onHasLoggedIn() {
        userInfo = VietKitchenApp.userInfo
        presenter.requestLikedRecipesId(userInfo.uid)
    }

    override fun onHasNotLoggedIn() {
    }

    override fun onRequestLikedRecipesIdSuccess(recipesId: List<String>) {
        userInfo.likedRecipesIds = recipesId.toMutableList()
        userInfo.numberFavoriteRecipes = recipesId.size
        gotoNextScreen()
    }

    override fun onRequestLikedRecipesIdFailed() {
    }
    //endregion MVP callbacks

    //region inner methods =========================================================================
    private fun gotoNextScreen(){
        val currentTime = System.currentTimeMillis()
        val duration = currentTime - timeStartedSplashScreen
        if (duration >= TIME_WAITING_IN_SPLASH_SCREEN){
            gotoHomeScreen()
        }else{
            val delayTime = TIME_WAITING_IN_SPLASH_SCREEN - duration
            FlowableEmpty.just(true)
                    .delay(delayTime, TimeUnit.MILLISECONDS)
                    .subscribe {
                        gotoHomeScreen()
                    }
        }
    }
    private fun gotoHomeScreen(){
        startActivityWithAnimation(HomeActivity.createIntent(this))
        finish()
    }
    //endregion inner methods
}