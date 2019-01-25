package com.example.linh.vietkitchen.ui.baseMVVM

abstract class FullScreenFragment : BaseFragment() {

    override fun onResume() {
        super.onResume()
        mFullScreenFragmentChangeCallbacks.onRequireFullScreen()
    }
}