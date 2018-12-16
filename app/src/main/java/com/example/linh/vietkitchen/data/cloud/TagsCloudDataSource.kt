package com.example.linh.vietkitchen.data.cloud

import com.example.linh.vietkitchen.data.response.Response
import com.example.linh.vietkitchen.domain.datasource.TagsDataSource
import com.example.linh.vietkitchen.domain.datasource.addListenerForSingleValueEventAwait
import com.example.linh.vietkitchen.domain.datasource.setValueAwait
import com.example.linh.vietkitchen.util.Constants
import com.example.linh.vietkitchen.util.ResponseCode.RESPONSE_SUCCESS
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class TagsCloudDataSource : TagsDataSource{
    private val database by lazy { FirebaseDatabase.getInstance() }
    private val dbRefRecipe by lazy { database.getReference(Constants.STORAGE_RECIPES_TAGS_PATH) }

    override suspend fun getTags(): Response<DataSnapshot>? {
        val dataSnapshot = dbRefRecipe.addListenerForSingleValueEventAwait()
        return Response(RESPONSE_SUCCESS, dataSnapshot)
    }

    override suspend fun putTags(tags: Map<String, Boolean>) : Response<Boolean> {
            tags.forEach {tag ->
                dbRefRecipe.child(tag.key).setValueAwait(tag.value)
            }
        return Response(RESPONSE_SUCCESS, true)
    }
}