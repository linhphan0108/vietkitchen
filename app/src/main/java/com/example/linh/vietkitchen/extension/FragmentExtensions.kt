package com.example.linh.vietkitchen.extension

import androidx.annotation.StringRes
import com.google.android.material.snackbar.Snackbar
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Toast
import com.example.linh.vietkitchen.R

fun androidx.fragment.app.Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT){
    context?.toast(message, length)
}

fun androidx.fragment.app.Fragment.showSnackBar(view: View, @StringRes resId: Int, length: Int = com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
                                                action: String = getString(R.string.default_snack_bar_action),
                                                listener: View.OnClickListener? = null){
    context?.showSnackBar(view, resId, length, action, listener)
}
fun androidx.fragment.app.Fragment.showSnackBar(view: View, message: String, length: Int = com.google.android.material.snackbar.Snackbar.LENGTH_LONG,
                                                action: String = getString(R.string.default_snack_bar_action),
                                                listener: View.OnClickListener? = null){
    context?.showSnackBar(view, message, length, action, listener)
}