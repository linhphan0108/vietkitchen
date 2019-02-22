package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.provider.UserProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class PutUnlikeCommand @Inject constructor(private val userProvider: UserProvider)
    : CommandCoroutines<Boolean>{
    lateinit var uid: String
    lateinit var recipeId: String

    override fun execute(context: Context): LiveData<Resource<Boolean>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute(): LiveData<Resource<Boolean>> {
        return userProvider.unLikeRecipe(uid, recipeId)
    }
}