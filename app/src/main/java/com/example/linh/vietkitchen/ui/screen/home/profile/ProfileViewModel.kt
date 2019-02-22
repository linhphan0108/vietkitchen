package com.example.linh.vietkitchen.ui.screen.home.profile

import android.app.Application
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.vo.Resource
import com.firebase.ui.auth.AuthUI
import javax.inject.Inject

class ProfileViewModel @Inject constructor(application: Application) : BaseViewModel(application) {

    private val _logoutStatus: MutableLiveData<Resource<Boolean>> = MutableLiveData()
    internal val logoutStatus: LiveData<Resource<Boolean>> = _logoutStatus

    fun onAllowNotificationChanged(hasAllow: Boolean) {

    }

    fun logout() {
        AuthUI.getInstance()
                .signOut(getApplication())
                .addOnCompleteListener {
                    _logoutStatus.value = Resource.success(true)
                }.addOnFailureListener {
                    _logoutStatus.value = Resource.error(it.message)
                }
    }
}