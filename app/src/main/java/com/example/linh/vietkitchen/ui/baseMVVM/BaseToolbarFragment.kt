package com.example.linh.vietkitchen.ui.baseMVVM

import android.content.Context

abstract class BaseToolbarFragment : BaseFragment() {
    protected var toolbarActions: ToolbarActions? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is ToolbarActions){
            toolbarActions = context
        }else{
            throw RuntimeException("$context must implement ${ToolbarActions::class.java}")
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