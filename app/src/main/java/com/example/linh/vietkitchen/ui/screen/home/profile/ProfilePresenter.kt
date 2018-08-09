package com.example.linh.vietkitchen.ui.screen.home.profile

import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import com.firebase.ui.auth.AuthUI

class ProfilePresenter : BasePresenter<ProfileContractView>(), ProfileContractPresenter {
    override fun logout() {
        AuthUI.getInstance()
                .signOut(context!!)
                .addOnCompleteListener {
                    viewContract?.onLogoutSuccess()
                }.addOnFailureListener {
                    viewContract?.onLogoutFailed()
                }
    }
}