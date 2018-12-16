package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.TagsProvider

class PutTagsCommand(private val provider: TagsProvider = TagsProvider()) : CommandCoroutines<Response<Boolean>> {
    lateinit var tags: Map<String, Boolean>

    override suspend fun executeOnTheInternet(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<Boolean> = provider.putTags(tags)
}