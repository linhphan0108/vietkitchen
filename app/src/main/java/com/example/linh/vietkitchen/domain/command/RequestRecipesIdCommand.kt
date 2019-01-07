package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.provider.UserProvider

class RequestRecipesIdCommand(private val userProvider: UserProvider = UserProvider())
    : CommandCoroutines<List<String>> {
    lateinit var uid: String

    override suspend fun execute(): List<String> {
        return userProvider.requestLikedRecipesId(uid)
    }

    override suspend fun execute(context: Context): List<String> {
        isInternetOn(context)
        return execute()
    }
}