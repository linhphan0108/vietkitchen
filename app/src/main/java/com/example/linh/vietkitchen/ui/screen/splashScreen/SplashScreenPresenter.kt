package com.example.linh.vietkitchen.ui.screen.splashScreen

import android.app.Activity
import android.content.Intent
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.command.RequestRecipesIdCommand
import com.example.linh.vietkitchen.exception.NoInternetConnection
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.ErrorCodes
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit

class SplashScreenPresenter(private val requestRecipesIdCommand: RequestRecipesIdCommand = RequestRecipesIdCommand())
    : BasePresenter<SplashScreenContractView>(), SplashScreenContractPresenter {
    companion object {
        private const val RC_SIGN_IN = 123
        private const val TIME_WAITING_IN_SPLASH_SCREEN = 7000//in millisecond
    }

    private var timeStartedSplashScreen: Long = 0

    override fun attachView(view: SplashScreenContractView) {
        super.attachView(view)
        timeStartedSplashScreen = System.currentTimeMillis()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
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
                viewContract?.onHasLoggedIn()
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

    override fun checkLogin() {
        if (FirebaseAuth.getInstance().currentUser != null){
            viewContract?.onHasLoggedIn()
        }else {
            silentLogin()
        }
    }

    override fun gotoNextScreen(){
        val currentTime = System.currentTimeMillis()
        val duration = currentTime - timeStartedSplashScreen
        if (duration >= TIME_WAITING_IN_SPLASH_SCREEN){
            viewContract?.gotoHomeScreen()
        }else{
            val delayTime = TIME_WAITING_IN_SPLASH_SCREEN - duration
            launchDataLoad({
                delay(delayTime)
                viewContract?.gotoHomeScreen()
            })
        }
    }

    private fun getSelectedProviders() = Arrays.asList(
            AuthUI.IdpConfig.EmailBuilder().build(),
            AuthUI.IdpConfig.PhoneBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build()
//                AuthUI.IdpConfig.FacebookBuilder().build()
//                AuthUI.IdpConfig.TwitterBuilder().build()
    )

    private fun silentLogin() {
        Timber.d("login silently")
        val providers = getSelectedProviders()
        AuthUI.getInstance().silentSignIn(context!!, providers)
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
                        viewContract?.onHasLoggedIn()
                        // Signed in! Start loading data
                    } else {
                        // Uh oh, show error message
                        Timber.e("log in failed ${it.exception?.message}")
                        loginNormally()
                    }
                }
    }

    private fun loginNormally(){
        Timber.d("login normally")
        // Choose authentication providers
        val providers = getSelectedProviders()
        // Create and launch sign-in intent
        (viewContract?.viewContext as Activity).startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(true, true)
                        .setTheme(R.style.NoTranslucentStatusBar)
                        .build(),
                RC_SIGN_IN)
    }

    override fun requestLikedRecipesId(uid: String) {
        launchDataLoad({
            val recipesId = withIoContext {
                requestRecipesIdCommand.uid = uid
                requestRecipesIdCommand.executeOnTheInternet(context!!)
            }
            viewContract?.onRequestLikedRecipesIdSuccess(recipesId)
        }, {e->
            when(e){
                is NoInternetConnection ->{
                    viewContract?.onNoInternetException()
                }
                else -> {
                    viewContract?.onRequestLikedRecipesIdFailed(e.message)

                }
            }
        })
    }
}