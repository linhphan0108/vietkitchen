package com.example.linh.vietkitchen.ui.baseMVVM

abstract class AbsJustToolbarFragment : BaseFragment() {
    override fun onResume() {
        super.onResume()
        mFullScreenFragmentChangeCallbacks.onRequireJustToolbarScreen()
    }

    protected fun getToolbar() = getHomeActivity().getToolbar()

    protected fun getAppbar() = getHomeActivity().getAppbar()
}