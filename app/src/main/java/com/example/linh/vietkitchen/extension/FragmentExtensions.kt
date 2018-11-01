package com.example.linh.vietkitchen.extension

import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.view.View
import android.widget.Toast
import com.example.linh.vietkitchen.R

fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT){
    context?.toast(message, length)
}

fun Fragment.showSnackBar(view: View, @StringRes resId: Int, length: Int = Snackbar.LENGTH_LONG,
                          action: String = getString(R.string.default_snack_bar_action),
                          listener: View.OnClickListener? = null){
    context?.showSnackBar(view, resId, length, action, listener)
}
fun Fragment.showSnackBar(view: View, message: String, length: Int = Snackbar.LENGTH_LONG,
                          action: String = getString(R.string.default_snack_bar_action),
                          listener: View.OnClickListener? = null){
    context?.showSnackBar(view, message, length, action, listener)
}