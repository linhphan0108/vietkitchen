package com.example.linh.vietkitchen.ui.screen.login

import android.content.Context
import android.content.Intent
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.ui.mvpBase.BaseActivity




class LoginActivity : BaseActivity<LoginContractView, LoginPresenter>(),
        LoginContractView{

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    //region MVP callbacks =========================================================================
    override val viewContext: Context?
        get() = this

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun initPresenter() = LoginPresenter()

    override fun getViewContract() = this

    override fun getActivityLayoutRes() = R.layout.activity_login
    //endregion MVP callbacks
}