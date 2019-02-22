package com.example.linh.vietkitchen.domain.command

import android.content.Context
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.domain.provider.TagsProvider
import com.example.linh.vietkitchen.vo.Resource
import javax.inject.Inject

class PutTagsCommand @Inject constructor(private val provider: TagsProvider)
    : CommandCoroutines<Boolean> {
    lateinit var tags: Map<String, Boolean>

    override fun execute(context: Context): LiveData<Resource<Boolean>> {
        isInternetOn(context)
        return execute()
    }

    override fun execute() = provider.putTags(tags)
}