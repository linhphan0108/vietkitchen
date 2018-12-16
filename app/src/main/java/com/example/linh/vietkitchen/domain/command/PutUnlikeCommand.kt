package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.UserProvider

class PutUnlikeCommand(private val userProvider: UserProvider = UserProvider()) : CommandCoroutines<Response<Boolean>>{
    lateinit var uid: String
    lateinit var recipeId: String

    override suspend fun executeOnTheInternet(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<Boolean> {
        return userProvider.unLikeRecipe(uid, recipeId)
    }
}