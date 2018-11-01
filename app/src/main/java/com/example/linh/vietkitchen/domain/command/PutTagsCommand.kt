package com.example.linh.vietkitchen.domain.command

import com.example.linh.vietkitchen.domain.provider.TagsProvider
import io.reactivex.Completable

class PutTagsCommand(private val provider: TagsProvider = TagsProvider()) : CommandCompletable {
    lateinit var tags: Map<String, Boolean>
    override fun execute(): Completable = provider.putTags(tags)
}