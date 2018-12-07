package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.provider.UserProvider
import io.reactivex.Flowable

class RequestRecipesIdCommand(private val userProvider: UserProvider = UserProvider())
    : CommandFollowable<List<String>> {
    lateinit var uid: String

    override fun execute(): Flowable<out List<String>> {
        return userProvider.requestLikedRecipesId(uid)
    }

    override fun executeOnTheInternet(context: Context): Flowable<out List<String>> {
        return isInternetOn(context)
                .flatMap { execute() }
    }
}