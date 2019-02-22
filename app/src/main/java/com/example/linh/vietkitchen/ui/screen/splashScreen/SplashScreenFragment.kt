package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.app.Activity
import androidx.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.navigation.fragment.findNavController
import com.example.linh.vietkitchen.BuildConfig
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.showSnackBar
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.FullScreenFragment
import com.firebase.ui.auth.AuthUI
import kotlinx.android.synthetic.main.activity_splash_screen.*
import timber.log.Timber
import com.example.linh.vietkitchen.di.injector
import com.example.linh.vietkitchen.di.viewModel
import com.example.linh.vietkitchen.vo.Status.*
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth


class SplashScreenFragment : FullScreenFragment() {
    companion object {
        private const val RC_SIGN_IN = 123
        private const val TIME_WAITING_IN_SPLASH_SCREEN = 7000//in millisecond
        private const val MAX_TIME_TO_RETRY = 3
        private const val ARG_SPLASH_SCREEN_TIME_STARTED = "ARG_SPLASH_SCREEN_TIME_STARTED"
        private const val ARG_RETRY_REQUEST_CATEGORY_TIMES = "ARG_RETRY_REQUEST_CATEGORY_TIMES"
        private const val ARG_RETRY_REQUEST_RECIPES_IDS_TIMES = "ARG_RETRY_REQUEST_RECIPES_IDS_TIMES"
    }

    private val viewModel: SplashScreenViewModel by viewModel(this){ injector.splashScreenFragmentViewModel }
    private var timeStartedSplashScreen: Long = System.currentTimeMillis()
    private var retryRequestCategoryTimes = 0
    private var retryRequestListLikedRecipesIdsTimes = 0

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        savedInstanceState?.let {
            timeStartedSplashScreen = it.getLong(ARG_SPLASH_SCREEN_TIME_STARTED)
            retryRequestCategoryTimes = it.getInt(ARG_RETRY_REQUEST_CATEGORY_TIMES)
            retryRequestListLikedRecipesIdsTimes = it.getInt(ARG_RETRY_REQUEST_RECIPES_IDS_TIMES)
        }
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

    //endregion lifecycle callbacks

    //region MVP callbacks =========================================================================
    override fun getFragmentLayoutRes() = R.layout.activity_splash_screen

    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun observeViewModel() {
        viewModel.silentLoginStatus.observe(this, androidx.lifecycle.Observer { resource ->
            when(resource.status){
                SUCCESS -> {
                    onHasLoggedIn()
                }
                //Status.ERROR_NO_INTERNET -> {onNoInternetException()}
                ERROR -> {
                    onHasNotLoggedIn()
                }

                LOADING -> {}
            }
        })
    }

    private fun onNoInternetException() {
        txtDotLoader.visibility = View.INVISIBLE
    }

    private fun onHasLoggedIn() {
        requestCategory()
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
//        if (retryRequestListLikedRecipesIdsTimes <= MAX_TIME_TO_RETRY) {
//            retryRequestListLikedRecipesIdsTimes++
//            val handler = Handler()
//            handler.postDelayed({
//                requestLikedRecipesId()
//            }, 1000 * retryRequestListLikedRecipesIdsTimes.toLong())
//            showSnackBar(root, "can not fetch the liked recipes because of $message\nretry $retryRequestListLikedRecipesIdsTimes times")
//        }else{
            showSnackBar(root, "can not fetch the liked recipes because of $message")
//        }
    }

    private fun onRequestCategoriesFailed(message: String?) {
//        if (retryRequestCategoryTimes <= MAX_TIME_TO_RETRY) {
//            retryRequestCategoryTimes++
//            val handler = Handler()
//            handler.postDelayed({
//                requestCategory()
//            }, 1000 * retryRequestCategoryTimes.toLong())
//            showSnackBar(root, "can not fetch the category because of $message\nretry $retryRequestCategoryTimes times")
//        }else{
            showSnackBar(root, "can not fetch the category because of $message")
//        }
    }

    private fun gotoHomeScreen(){
        findNavController().navigate(R.id.action_splash_screen_dest_to_home_dest, null,
                null)
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

    private fun requestCategory(){
        viewModel.requestCategory().observe(this, Observer { resource ->
            when(resource.status){
                SUCCESS -> {
                    requestLikedRecipesId()
                }
                LOADING -> {

                }
                ERROR -> {
                    onRequestCategoriesFailed(resource.message)
                }
            }
        })
    }

    private fun requestLikedRecipesId(){
        viewModel.requestLikedRecipesId().observe(this, Observer { resource ->
            when(resource.status){
                LOADING -> {}
                ERROR -> {onRequestLikedRecipesIdFailed(resource.message)}
                SUCCESS -> {gotoNextScreen()}
            }
        })
    }
    //endregion inner methods
}