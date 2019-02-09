package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.provider.TagsProvider
import javax.inject.Inject

class PutTagsCommand @Inject constructor(private val provider: TagsProvider)
    : CommandCoroutines<Response<Boolean>> {
    lateinit var tags: Map<String, Boolean>

    override suspend fun execute(context: Context): Response<Boolean> {
        isInternetOn(context)
        return execute()
    }

    override suspend fun execute(): Response<Boolean> = provider.putTags(tags)
}