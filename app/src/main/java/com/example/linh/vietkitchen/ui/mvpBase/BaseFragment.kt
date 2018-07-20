package com.example.linh.vietkitchen.ui.mvpBase

import android.content.Context
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.extension.DelegatesExt
import timber.log.Timber

abstract class BaseFragment<T : BaseViewContract, V : BasePresenter<T>> : Fragment(){
    protected var presenter: V by DelegatesExt.notNullSingleValue()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = initPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.e("on create view")
        presenter.attachView(getViewContract())
        return inflater.inflate(getFragmentLayoutRes(), container, false)

    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    abstract fun initPresenter(): V

    abstract fun getViewContract() : T

    abstract fun getFragmentLayoutRes(): Int
}