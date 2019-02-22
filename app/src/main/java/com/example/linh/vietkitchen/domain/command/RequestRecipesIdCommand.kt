package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.provider.UserProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class RequestRecipesIdCommand @Inject constructor(
        private val userProvider: UserProvider)
    : CommandCoroutines<List<String>> {
    lateinit var uid: String

    override fun execute() = userProvider.requestLikedRecipesId(uid)

    override fun execute(context: Context): LiveData<Resource<List<String>>> {
        isInternetOn(context)
        return execute()
    }
}