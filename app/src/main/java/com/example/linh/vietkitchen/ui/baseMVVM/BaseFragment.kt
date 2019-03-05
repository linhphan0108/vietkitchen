package com.example.linh.vietkitchen.ui.baseMVVM

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.linh.vietkitchen.ui.dialog.LoadingDialog
import com.example.linh.vietkitchen.ui.screen.home.homeActivity.HomeActivity
import timber.log.Timber
abstract class BaseFragment : Fragment() {

    private var loadingDialog: LoadingDialog? = null
    protected lateinit var mFullScreenFragmentChangeCallbacks: FragmentScreenChangeCallbacks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getViewModel()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is BaseActivity){
            throw RuntimeException("the host activity $context should be ${BaseActivity::class.java} or it's derived classes")
        }
        if (context !is FragmentScreenChangeCallbacks)
            throw ClassCastException("the host $context must implement the ${FragmentScreenChangeCallbacks::class.java} interface")
        mFullScreenFragmentChangeCallbacks = context
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        Timber.e("on create view")
        return inflateFragmentView(inflater, container, savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        observeViewModel()
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

    internal fun getHomeActivity(): HomeActivity = activity as HomeActivity

    protected fun setTitle(title: String){
        getHomeActivity().supportActionBar?.title = title
    }

    //======= inner classes ========================================================================
    interface FragmentScreenChangeCallbacks{
        fun onRequireFullScreen()
        fun onRequireNormalScreen()
        fun onRequireJustToolbarScreen()
    }
}