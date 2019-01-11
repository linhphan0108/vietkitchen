package com.example.linh.vietkitchen.ui.screen.login

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.baseMVVM.BaseActivity
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel


class LoginActivity : BaseActivity(){
    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private lateinit var viewModel: LoginViewModel

    //region MVP callbacks =========================================================================
    override fun getActivityLayoutRes() = R.layout.activity_login
    override fun getViewModel(): BaseViewModel {
        val factory = LogInViewModelFactory(application)
        viewModel = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        return viewModel
    }

    override fun observeViewModel() {
    }
    //endregion MVP callbacks
}