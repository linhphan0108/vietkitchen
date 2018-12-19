package com.example.linh.vietkitchen.ui.model

import android.os.Parcel
import android.os.Parcelable
import com.example.linh.vietkitchen.extension.readListParcelableObjects
import com.example.linh.vietkitchen.extension.writeListParcelableObjects

class DrawerNavGroupItem(val headerTile: String, val path: String, var numberItems: Int, val itemsList: List<DrawerNavChildItem>? = null,
                         var isChildrenVisible: Boolean = false)
    : Entity(), Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readListParcelableObjects(DrawerNavChildItem::class.java),
            parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(headerTile)
        parcel.writeString(path)
        parcel.writeInt(numberItems)
        parcel.writeListParcelableObjects(itemsList)
        parcel.writeByte(if (isChildrenVisible) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DrawerNavGroupItem> {
        override fun createFromParcel(parcel: Parcel): DrawerNavGroupItem {
            return DrawerNavGroupItem(parcel)
        }

        override fun newArray(size: Int): Array<DrawerNavGroupItem?> {
            return arrayOfNulls(size)
        }
    }

    fun clone(): DrawerNavGroupItem{
        return DrawerNavGroupItem(headerTile, path, numberItems, itemsList?.map { it.clone()}, isChildrenVisible)
    }
}