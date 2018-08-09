package com.example.linh.vietkitchen.ui.screen.home.profile

import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface ProfileContractView : BaseViewContract{
    fun onLogoutSuccess()
    fun onLogoutFailed()
}

interface ProfileContractPresenter : BasePresenterContract<ProfileContractView>{
    fun logout()
}