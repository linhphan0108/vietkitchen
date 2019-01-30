package com.example.linh.vietkitchen.ui.baseMVVM

abstract class AbsFullNavFragment : BaseFragment() {
    override fun onResume() {
        super.onResume()
        mFullScreenFragmentChangeCallbacks.onRequireNormalScreen()
    }

    protected fun getToolbar() = getHomeActivity().getToolbar()

    protected fun getAppbar() = getHomeActivity().getAppbar()
}