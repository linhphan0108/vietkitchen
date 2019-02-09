package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.provider.UserProvider
import javax.inject.Inject

class RequestRecipesIdCommand @Inject constructor(
        private val userProvider: UserProvider)
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