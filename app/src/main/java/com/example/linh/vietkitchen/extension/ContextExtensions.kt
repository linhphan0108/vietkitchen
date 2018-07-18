package com.example.linh.vietkitchen.extension

import android.content.Context
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.widget.Toast

fun Context.color(res: Int): Int = ContextCompat.getColor(this, res)
fun Context.toast(message: String, length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this, message, length).show()
}
fun Fragment.toast(message: String, length: Int = Toast.LENGTH_SHORT){
    Toast.makeText(this.context, message, length).show()
}