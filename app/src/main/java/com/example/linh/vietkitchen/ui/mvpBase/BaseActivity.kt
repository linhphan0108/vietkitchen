package com.example.linh.vietkitchen.ui.mvpBase

import android.os.Bundle
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity<T : BaseViewContract, V : BasePresenter<T>> : AppCompatActivity() {
    protected lateinit var presenter: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter.attachView(getViewContract())
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    abstract fun getViewContract() : T
}