package com.example.linh.vietkitchen.ui.mvpBase

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.v7.app.AppCompatActivity
import android.support.v7.graphics.Palette
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.extension.color
import kotlinx.android.synthetic.main.activity_detail.*

abstract class BaseActivity<T : BaseViewContract, V : BasePresenterContract<T>> : AppCompatActivity() {
    protected lateinit var presenter: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getActivityLayoutRes())
        presenter = initPresenter()
        presenter.attachView(getViewContract())
    }

    override fun onDestroy() {
        presenter.detachView()
        super.onDestroy()
    }

    abstract fun initPresenter(): V

    abstract fun getViewContract() : T

    abstract fun getActivityLayoutRes(): Int

    protected fun startActivityWithAnimation(intent: Intent){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val activityOptions = ActivityOptions.makeCustomAnimation(this, R.anim.translate_enter_right_to_left, R.anim.delay)
            startActivity(intent, activityOptions.toBundle())
        } else {
            startActivity(intent)
        }
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
}