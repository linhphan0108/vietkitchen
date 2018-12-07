package com.example.linh.vietkitchen.domain.command

import android.content.Context
import com.example.linh.vietkitchen.domain.provider.TagsProvider
import io.reactivex.Flowable

class RequestTagsCommand(private val provider: TagsProvider = TagsProvider()) : CommandFollowable<Map<String, Boolean>> {

    override fun executeOnTheInternet(context: Context): Flowable<out Map<String, Boolean>> {
        return isInternetOn(context)
                .flatMap { execute() }
    }

    override fun execute(): Flowable<Map<String, Boolean>> = provider.getTags()
}