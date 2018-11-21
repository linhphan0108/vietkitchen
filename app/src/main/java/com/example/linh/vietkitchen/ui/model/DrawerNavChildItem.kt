package com.example.linh.vietkitchen.ui.model

import android.os.Parcel
import android.os.Parcelable

class DrawerNavChildItem(val itemTitle: String, val path: String, var numberItems: Int)
    : Entity(), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(itemTitle)
        parcel.writeString(path)
        parcel.writeInt(numberItems)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DrawerNavChildItem> {
        override fun createFromParcel(parcel: Parcel): DrawerNavChildItem {
            return DrawerNavChildItem(parcel)
        }

        override fun newArray(size: Int): Array<DrawerNavChildItem?> {
            return arrayOfNulls(size)
        }
    }
}