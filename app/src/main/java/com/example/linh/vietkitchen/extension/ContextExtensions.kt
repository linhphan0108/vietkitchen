package com.example.linh.vietkitchen.extension

import android.content.Context
import android.content.res.Resources
import android.support.annotation.StringRes
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.view.View
import android.widget.Toast
import com.example.linh.vietkitchen.R

fun Context.color(res: Int): Int = ContextCompat.getColor(this, res)
fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, message, length).show()
}
fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT){
    context?.toast(message, length)
}

fun Context.showSnackBar(view: View, @StringRes resId: Int, length: Int = Snackbar.LENGTH_LONG,
                         action: String = getString(R.string.default_snack_bar_action),
                         listener: View.OnClickListener? = null){
    Snackbar.make(view, resId, length)
            .setAction(action, listener).show()
}

fun Context.showSnackBar(view: View, message: String, length: Int = Snackbar.LENGTH_LONG,
                         action: String = getString(R.string.default_snack_bar_action),
                         listener: View.OnClickListener? = null){
    Snackbar.make(view, message, length)
            .setAction(action, listener).show()
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