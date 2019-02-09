package com.example.linh.vietkitchen.ui.screen.home.profile

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.baseMVVM.StatusBox
import com.firebase.ui.auth.AuthUI
import javax.inject.Inject

class ProfileViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    internal val logoutStatus: MutableLiveData<StatusBox<Boolean>> = MutableLiveData()

    fun onAllowNotificationChanged(hasAllow: Boolean) {

    }

    fun logout() {
        AuthUI.getInstance()
                .signOut(getApplication())
                .addOnCompleteListener {
                    logoutStatus.value = StatusBox(Status.SUCCESS, data = true)
                }.addOnFailureListener {
                    logoutStatus.value = StatusBox(Status.ERROR, it.message, data = false)
                }
    }
}