package com.example.linh.vietkitchen.ui.baseMVVM

import android.app.ActivityOptions
import android.arch.lifecycle.Observer
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.color
import com.example.linh.vietkitchen.ui.dialog.LoadingDialog

abstract class BaseActivity : AppCompatActivity() {
    private val loadingDialog: LoadingDialog by lazy { LoadingDialog.newInstance(getString(R.string.label_loading)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityLayoutRes())
        getViewModel()
        observeLoadingDialogState()
        observeViewModel()
    }

    abstract fun getActivityLayoutRes(): Int

    abstract fun  getViewModel(): BaseViewModel
    abstract fun observeViewModel()

    internal fun showProgress() {
        loadingDialog.show(supportFragmentManager, LoadingDialog::class.java.name)
    }

    internal fun hideProgress() {
        if(loadingDialog.isVisible) loadingDialog.dismiss()
    }

    protected fun startActivityWithAnimation(intent: Intent){
        val activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.translate_enter_right_to_left, R.anim.delay)
        startActivity(intent, activityOptions.toBundle())
    }

    internal fun applyPalette(palette: Palette?, collapsingToolbarLayout: CollapsingToolbarLayout) {
//        val transparent = color(android.R.color.transparent)
        var mutedPrimaryDark = color(R.color.colorPrimaryDark)
        var mutedPrimary = color(R.color.colorPrimary)
        var expandedTitleTextColor = color(R.color.textAccent)
        var collapsedTitleTextColor = color(R.color.colorOnPrimary)
        if(palette != null) {
            val mutedSwatch = palette.mutedSwatch
            mutedPrimary = mutedSwatch?.rgb ?: mutedPrimary
            collapsedTitleTextColor = mutedSwatch?.titleTextColor ?: collapsedTitleTextColor
            expandedTitleTextColor = mutedSwatch?.titleTextColor ?: expandedTitleTextColor
            mutedPrimaryDark = palette.getDarkMutedColor(mutedPrimaryDark)
        }

        collapsingToolbarLayout.setContentScrimColor(mutedPrimary)
        collapsingToolbarLayout.setStatusBarScrimColor(mutedPrimaryDark)
        collapsingToolbarLayout.setCollapsedTitleTextColor(collapsedTitleTextColor)
        collapsingToolbarLayout.setExpandedTitleColor(expandedTitleTextColor)
    }

    private fun observeLoadingDialogState(){
        getViewModel().loadingDialogState.observe(this, Observer { loadingDialogVisibility ->
            if (loadingDialogVisibility!!){
                showProgress()
            }else{
                hideProgress()
            }
        })
    }
}