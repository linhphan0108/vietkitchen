package com.example.linh.vietkitchen.ui.model

import android.os.Parcel
import android.os.Parcelable

class SearchItem(val query: String, val type: SearchItemType) : Entity(), Parcelable {
    enum class SearchItemType{
        TITLE, CATEGORY, TAG
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(query)
        parcel.writeString(type.name)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<SearchItem> {
        override fun createFromParcel(parcel: Parcel): SearchItem {
            val title = parcel.readString() ?: ""
            val type = SearchItemType.valueOf(parcel.readString()!!)
            return SearchItem(title, type)
        }

        override fun newArray(size: Int): Array<SearchItem?> {
            return arrayOfNulls(size)
        }
    }
}