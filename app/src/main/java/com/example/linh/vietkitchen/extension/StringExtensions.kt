package com.example.linh.vietkitchen.extension

import android.text.SpannableStringBuilder


fun String.capWords(): String{
    if (isBlank()) return this
    val strArray = this.split(" ")
    val result = StringBuilder()
    for (s in strArray) {
        val cap = s.substring(0, 1).toUpperCase() + s.substring(1)
        result.append("$cap ")
    }
    return if (result.isEmpty()) ""
    else result.substring(0, result.length - 1)
}

//fun String.capFirstWorld(): String{
//    if (isEmpty()) return this
//    val firstChar = substring(0, 1).capitalize()
//}

fun String.generateAnnotations(): CharSequence{
    val ssb = SpannableStringBuilder()

    return ssb
}