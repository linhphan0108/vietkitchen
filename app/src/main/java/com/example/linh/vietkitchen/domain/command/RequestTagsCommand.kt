package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.provider.TagsProvider
import io.reactivex.Flowable

class RequestTagsCommand(private val provider: TagsProvider = TagsProvider()) : CommandFollowable<Map<String, Boolean>> {
    override fun execute(): Flowable<Map<String, Boolean>> = provider.getTags()
}