package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.TagsProvider

class RequestTagsCommand(private val provider: TagsProvider = TagsProvider()) : CommandCoroutines<Response<Map<String, Boolean>>> {

    override suspend fun execute(context: Context): Response<Map<String, Boolean>> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<Map<String, Boolean>> = provider.getTags()
}