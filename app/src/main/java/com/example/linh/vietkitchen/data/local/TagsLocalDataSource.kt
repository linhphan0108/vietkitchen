package com.example.linh.vietkitchen.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class TagsLocalDataSource {
    fun getTags(): LiveData<Map<String, Boolean>?> {
        return MutableLiveData<Map<String, Boolean>?>().apply { value = null }
    }

    fun putTags(tags: Map<String, Boolean>): LiveData<Boolean?> {
        return MutableLiveData<Boolean?>().apply { value = false }
    }
}