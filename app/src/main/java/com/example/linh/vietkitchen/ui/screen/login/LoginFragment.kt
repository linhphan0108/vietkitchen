package com.example.linh.vietkitchen.ui.screen.login

import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.FullScreenFragment
import com.example.linh.vietkitchen.ui.di.injector
import com.example.linh.vietkitchen.ui.di.viewModel


class LoginFragment : FullScreenFragment(){
    private val viewModel: LoginViewModel by viewModel(this){ injector.loginFragmentViewModel }

    override fun getFragmentLayoutRes() = R.layout.activity_login
    override fun getViewModel(): BaseViewModel {
        return viewModel
    }

    override fun observeViewModel() {
    }
    //endregion MVP callbacks
}