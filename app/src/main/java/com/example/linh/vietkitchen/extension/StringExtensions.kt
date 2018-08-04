package com.example.linh.vietkitchen.extension


fun String.capWords(): String{
    val strArray = this.split(" ")
    val result = StringBuilder()
    for (s in strArray) {
        val cap = s.substring(0, 1).toUpperCase() + s.substring(1)
        result.append("$cap ")
    }
    return if (result.isEmpty()) ""
    else result.substring(0, result.length - 1)
}