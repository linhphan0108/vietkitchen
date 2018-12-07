package com.example.linh.vietkitchen.ui.mvpBase

import android.content.Context

interface BasePresenterContract<V> {
    fun attachView(view: V)
    fun detachView()
}

interface BaseViewContract {
    val viewContext: Context?
    fun onNoInternetException()
    fun showProgress()
    fun hideProgress()
}