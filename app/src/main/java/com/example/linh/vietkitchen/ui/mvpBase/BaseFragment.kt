package com.example.linh.vietkitchen.ui.mvpBase

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.linh.vietkitchen.extension.DelegatesExt
import timber.log.Timber

abstract class BaseFragment<T : BaseViewContract, V : BasePresenterContract<T>> : Fragment(){
    protected var presenter: V by DelegatesExt.notNullSingleValue()


    override fun onAttach(context: Context) {
        super.onAttach(context)
        presenter = initPresenter()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.e("on create view")
        presenter.attachView(getViewContract())
        return inflateFragmentView(inflater, container, savedInstanceState)

    }

    override fun onDestroyView() {
        presenter.detachView()
        super.onDestroyView()
    }

    abstract fun initPresenter(): V

    abstract fun getViewContract() : T

    protected open fun inflateFragmentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(getFragmentLayoutRes(), container, false)

    abstract fun getFragmentLayoutRes(): Int
}