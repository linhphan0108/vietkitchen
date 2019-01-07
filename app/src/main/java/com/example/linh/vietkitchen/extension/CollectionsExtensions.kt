package com.example.linh.vietkitchen.extension

import java.io.File.separator
import java.util.*

fun <K, V : Any> Map<K, V?>.toVarargArray(): Array<out Pair<K, V>> =
        map { Pair(it.key, it.value!!) }.toTypedArray()

inline fun <T, R : Any> Iterable<T>.firstResult(predicate: (T) -> R?): R {
    for (element in this) {
        val result = predicate(element)
        if (result != null) return result
    }
    throw NoSuchElementException("No element matching predicate was found.")
}

inline fun <T, R : Any> Iterable<T>.lastResult(predicate: (T) -> R?): R {
    var result: R? = null
    forEach{ element ->
        val r = predicate(element)
        if (r != null) {
            result = r
        }
    }
    if (result != null) return result!!
    throw NoSuchElementException("No element matching predicate was found.")
}

inline fun <T> Iterable<T>.findIndex(predicate: (T) -> Boolean) : Int{
    forEachIndexed { index, t ->
        if (predicate(t)) return index
    }
    return -1
}

inline fun <T> Iterable<T>.filterFirst(predicate: (T) -> Boolean): T?{
    forEach { if(predicate(it)) return it }
    return null
}

fun List<String>.toString(separator: String): String{
    val result = StringBuilder()
    this.forEach {
        result.append("$it$separator")
    }
    return if (result.isEmpty()) ""
    else result.substring(0, result.length - separator.length)
}

fun List<String>.toMapOfStringBoolean(): MutableMap<String, Boolean> {
    val result = mutableMapOf<String, Boolean>()
    forEach {
        result[it] = true
    }
    return result
}

fun <T> MutableList<T>.removeLast(): Boolean{
    if (isNullOrEmpty()) throw KotlinNullPointerException("list must be not null or empty")
    return removeAt(size -1) != null
}

fun <K, V> Map<K, V>.toListOfStringOfKey(): List<K> {
    val result = mutableListOf<K>()
    forEach {
        result.add(it.key)
    }
    return result
}

fun <K, V> Map<K, V>.toArrayString(): Array<String>{
    val result = Array(size){""}
    var i = 0
    forEach {
        result[i] = it.value.toString()
        i++
    }
    return result
}