package com.example.linh.vietkitchen.ui.mvpBase

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.example.linh.vietkitchen.R

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
}