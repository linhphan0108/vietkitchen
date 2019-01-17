package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.app.Activity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.showSnackBar
import com.example.linh.vietkitchen.extension.toast
import com.example.linh.vietkitchen.ui.baseMVVM.BaseActivity
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_splash_screen.*
import timber.log.Timber
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class SplashScreenActivity : BaseActivity() {
    companion object {
        private const val RC_SIGN_IN = 123
        private const val TIME_WAITING_IN_SPLASH_SCREEN = 7000//in millisecond
        private const val MAX_TIME_TO_RETRY = 3
        private const val ARG_SPLASH_SCREEN_TIME_STARTED = "ARG_SPLASH_SCREEN_TIME_STARTED"
        private const val ARG_RETRY_REQUEST_CATEGORY_TIMES = "ARG_RETRY_REQUEST_CATEGORY_TIMES"
        private const val ARG_RETRY_REQUEST_RECIPES_IDS_TIMES = "ARG_RETRY_REQUEST_RECIPES_IDS_TIMES"
    }

    private lateinit var viewModel: SplashScreenViewModel
    private var timeStartedSplashScreen: Long = System.currentTimeMillis()
    private var retryRequestCategoryTimes = 0
    private var retryRequestListLikedRecipesIdsTimes = 0

    //region lifecycle callbacks ===================================================================
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        savedInstanceState?.let {
            timeStartedSplashScreen = it.getLong(ARG_SPLASH_SCREEN_TIME_STARTED)
            retryRequestCategoryTimes = it.getInt(ARG_RETRY_REQUEST_CATEGORY_TIMES)
            retryRequestListLikedRecipesIdsTimes = it.getInt(ARG_RETRY_REQUEST_RECIPES_IDS_TIMES)
        }
        slackLoadingView.start()
        txtAppNameVersion.text = getString(R.string.app_name_version, BuildConfig.VERSION_NAME)
        viewModel.checkLogin()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            val response = IdpResponse.fromResultIntent(data)
            // Successfully signed in
            if (resultCode == Activity.RESULT_OK) {
//                val idpResponse = response?.idpToken
                val metadata = FirebaseAuth.getInstance().currentUser?.metadata
                if (metadata?.creationTimestamp == metadata?.lastSignInTimestamp) {
                    // The user is new, show them a fancy intro screen!
                    Timber.d("logged in first time")
                } else {
                    // This is an existing user, show them a welcome back screen.
                }
//                startActivity(SignedInActivity.createIntent(this, response))
                onHasLoggedIn()
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                if (response == null) {
                    // User pressed back button
                    return
                }

                if (response.error!!.errorCode == ErrorCodes.NO_NETWORK) {
//                    showSnackbar(R.string.no_internet_connection)
                    return
                }

                Timber.e( "Sign-in error: ${response.error}")
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putLong(ARG_SPLASH_SCREEN_TIME_STARTED, timeStartedSplashScreen)
        outState.putInt(ARG_RETRY_REQUEST_CATEGORY_TIMES, retryRequestCategoryTimes)
        outState.putInt(ARG_RETRY_REQUEST_RECIPES_IDS_TIMES, retryRequestListLikedRecipesIdsTimes)
        super.onSaveInstanceState(outState)
    }

    override fun onBackPressed() {
        finish()
    }

    //endregion lifecycle callbacks

    //region MVP callbacks =========================================================================
    override fun getActivityLayoutRes() = R.layout.activity_splash_screen
    override fun getViewModel(): BaseViewModel {
        val factory = SplashScreenViewModelFactory(application)
        viewModel = ViewModelProviders.of(this, factory).get(SplashScreenViewModel::class.java)
        return viewModel
    }

    override fun observeViewModel() {
        viewModel.silentLoginStatus.observe(this, androidx.lifecycle.Observer { box ->
            box?.let {
                when(box.code){
                    Status.HAS_LOGGED_IN -> {
                        if (box.data!!){
                            onHasLoggedIn()
                        }else{
                            onHasNotLoggedIn()
                        }
                    }
                    Status.ERROR_NO_INTERNET -> {onNoInternetException()}
                }
            }
        })
        viewModel.likedRecipesId.observe(this, Observer {box ->
            box?.let {
                when(box.code){
                    Status.ERROR_NO_INTERNET -> {onNoInternetException()}
                    Status.ERROR -> {onRequestLikedRecipesIdFailed(box.message)}
                    Status.SUCCESS -> {gotoNextScreen()}
                }
            }
        })
        viewModel.requestNavStatus.observe(this, Observer {box ->
            box?.let {
                when(box.code){
                    Status.ERROR_NO_INTERNET -> {onNoInternetException()}
                    Status.ERROR -> {onRequestCategoriesFailed(box.message)}
                    Status.SUCCESS -> {viewModel.requestLikedRecipesId()}

                }
            }
        })
    }

    private fun onNoInternetException() {
        slackLoadingView.reset()
        txtDotLoader.visibility = View.INVISIBLE
    }

    private fun onHasLoggedIn() {
        viewModel.requestCategory()
    }

    private fun onHasNotLoggedIn() {
        requireNormallyLogin()
    }

    private fun requireNormallyLogin(){
        Timber.d("login normally")
        // Choose authentication providers
        val providers = viewModel.getSelectedProviders()
        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(true, true)
                        .setTheme(R.style.NoTranslucentStatusBar)
                        .build(),
                RC_SIGN_IN)
    }

    private fun onRequestLikedRecipesIdFailed(message: String?) {
        if (retryRequestListLikedRecipesIdsTimes <= MAX_TIME_TO_RETRY) {
            val handler = Handler()
            handler.postDelayed({
                viewModel.requestLikedRecipesId()
            }, 1000 * retryRequestListLikedRecipesIdsTimes.toLong())
            showSnackBar(root, "can not fetch the liked recipes because of $message\nretry $retryRequestListLikedRecipesIdsTimes times")
        }else{
            showSnackBar(root, "can not fetch the liked recipes because of $message")
        }
    }

    private fun onRequestCategoriesFailed(message: String?) {
        if (retryRequestCategoryTimes <= MAX_TIME_TO_RETRY) {
            retryRequestCategoryTimes++
            val handler = Handler()
            handler.postDelayed({
                viewModel.requestCategory()
            }, 1000 * retryRequestCategoryTimes.toLong())
            showSnackBar(root, "can not fetch the category because of $message\nretry $retryRequestCategoryTimes times")
        }else{
            showSnackBar(root, "can not fetch the category because of $message")
        }
    }

    private fun gotoHomeScreen(){
        slackLoadingView.reset()
        startActivityWithAnimation(HomeActivity.createIntent(this))
        finish()
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
            val handler = Handler()
            handler.postDelayed({
                gotoHomeScreen()
            }, delayTime)
        }
    }
    //endregion inner methods
}