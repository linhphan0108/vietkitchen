package com.example.linh.vietkitchen.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.data.cloud.Category
import com.example.linh.vietkitchen.domain.model.CategoryGroup

class CategoryLocalDs {
    fun updateCategories(category: Category): LiveData<Boolean?> {
        return MutableLiveData<Boolean?>().apply { postValue(false)}
    }

    fun getCategories(): LiveData<List<CategoryGroup>?> {
        return MutableLiveData<List<CategoryGroup>?>().apply { postValue(null) }
    }

}