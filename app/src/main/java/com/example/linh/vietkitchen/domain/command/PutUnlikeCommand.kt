package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.UserProvider
import javax.inject.Inject

class PutUnlikeCommand @Inject constructor(private val userProvider: UserProvider)
    : CommandCoroutines<Response<Boolean>>{
    lateinit var uid: String
    lateinit var recipeId: String

    override suspend fun execute(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<Boolean> {
        return userProvider.unLikeRecipe(uid, recipeId)
    }
}