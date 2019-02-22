package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.provider.UserProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class PutLikeCommand @Inject constructor(private val userProvider: UserProvider)
    : CommandCoroutines<String> {
    lateinit var uid: String
    lateinit var recipeId: String

    override fun execute(context: Context): LiveData<Resource<String>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute() = userProvider.likeRecipe(uid, recipeId)
}