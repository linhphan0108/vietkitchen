package com.example.linh.vietkitchen.ui.screen.login

import androidx.lifecycle.ViewModelProviders
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.FullScreenFragment


class LoginFragment : FullScreenFragment(){
    private lateinit var viewModel: LoginViewModel

    override fun getFragmentLayoutRes() = R.layout.activity_login
    override fun getViewModel(): BaseViewModel {
        val factory = LogInViewModelFactory(activity!!.application)
        viewModel = ViewModelProviders.of(this, factory).get(LoginViewModel::class.java)
        return viewModel
    }

    override fun observeViewModel() {
    }
    //endregion MVP callbacks
}