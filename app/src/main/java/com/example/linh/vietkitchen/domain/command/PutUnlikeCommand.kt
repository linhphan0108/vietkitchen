package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.provider.UserProvider
import io.reactivex.Completable

class PutUnlikeCommand(private val userProvider: UserProvider = UserProvider()) : CommandCompletable{
    lateinit var uid: String
    lateinit var recipeId: String

    override fun executeOnTheInternet(context: Context): Completable {
        return isInternetOn(context)
                .flatMapCompletable { execute() }
    }

    override fun execute(): Completable {
        return userProvider.unLikeRecipe(uid, recipeId)
    }
}