package com.example.linh.vietkitchen.ui.baseMVVM

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.linh.vietkitchen.ui.dialog.LoadingDialog
import timber.log.Timber
abstract class BaseFragment : Fragment() {
    private var loadingDialog: LoadingDialog? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getViewModel()
        observeViewModel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is BaseActivity){
            throw RuntimeException("the host activity should be BaseActivity or it's derived classes")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.e("on create view")
        return inflateFragmentView(inflater, container, savedInstanceState)
    }

    protected open fun inflateFragmentView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
            = inflater.inflate(getFragmentLayoutRes(), container, false)

    internal abstract fun getViewModel(): BaseViewModel
    internal abstract fun observeViewModel()
    internal abstract fun getFragmentLayoutRes(): Int

    //==== inner methods ===========================================================================
    fun showProgress() {
        if (loadingDialog == null) {
            loadingDialog = LoadingDialog.newInstance("delete")
        }
        loadingDialog!!.show(childFragmentManager, LoadingDialog::class.java.name)
    }

    fun hideProgress() {
        loadingDialog?.let { loadingDialog ->
            if(loadingDialog.isVisible) loadingDialog.dismiss()
        }
    }
}