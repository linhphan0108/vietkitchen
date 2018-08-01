package com.example.linh.vietkitchen.ui.mvpBase

import android.content.Context

abstract class ToolbarFragment<T : BaseViewContract, V : BasePresenterContract<T>> : BaseFragment<T, V>() {
    protected var toolbarActions: ToolbarActions? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ToolbarActions){
            toolbarActions = context
        }else{
            throw RuntimeException("$context must implement ToolbarActions")
        }
    }

    override fun onDetach() {
        super.onDetach()
        toolbarActions = null
    }
}

interface ToolbarActions{
    fun changeToolbarTitle(title: String)
}