package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.provider.UserProvider
import io.reactivex.Completable

class PutLikeCommand(private val userProvider: UserProvider = UserProvider()) : CommandCompletable {
    lateinit var uid: String
    lateinit var recipeId: String
    override fun execute(): Completable {
        return userProvider.likeRecipe(uid, recipeId)
    }
}