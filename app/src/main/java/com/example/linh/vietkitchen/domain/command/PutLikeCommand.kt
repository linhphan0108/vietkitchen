package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.UserProvider

class PutLikeCommand(private val userProvider: UserProvider = UserProvider())
    : CommandCoroutines<Response<String>> {
    lateinit var uid: String
    lateinit var recipeId: String

    override suspend fun execute(context: Context): Response<String> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<String> {
        return userProvider.likeRecipe(uid, recipeId)
    }
}