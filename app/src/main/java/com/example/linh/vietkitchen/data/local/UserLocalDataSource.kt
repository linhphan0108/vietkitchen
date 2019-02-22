package com.example.linh.vietkitchen.data.local

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class UserLocalDataSource{
    fun getLikedRecipesId(uid: String): LiveData<List<String>>{
        return MutableLiveData<List<String>>().apply {
            value = null
        }
    }

    fun likeRecipe(uid: String, recipeKey: String): LiveData<String>{
        return MutableLiveData<String>().apply {
            value = null
        }
    }

    fun unLikeRecipe(uid: String, recipeKey: String): LiveData<Boolean>{
        return MutableLiveData<Boolean>().apply {
            value = null
        }
    }
}