package com.example.linh.vietkitchen.extension

import android.os.Parcel
import android.os.Parcelable

fun <T: Parcelable> Parcel.readListParcelableObjects(c: Class<out T>): List<T>{
    val list = mutableListOf<T>()
    readList(list, c.classLoader)
    return list
}

fun <T: Parcelable> Parcel.writeListParcelableObjects(list: List<T>?){
    writeList(list)
}