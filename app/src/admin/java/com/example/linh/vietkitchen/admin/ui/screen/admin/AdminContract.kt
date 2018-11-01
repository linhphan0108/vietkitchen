package com.example.linh.vietkitchen.admin.ui.screen.admin

import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract


interface AdminContractView : BaseViewContract{
    fun onGetTagsSuccess(tags: List<String>)
    fun onGetTagsFailed(message: String?)
    fun onPutNewTagsSuccess()
    fun onPutNewTagsFailed(message: String?)
    fun onPutRecipeSuccess()
    fun onPutRecipeFailed(message: String?)
}

interface AdminContractPresenter: BasePresenterContract<AdminContractView>{
    fun preview(recipe: Recipe)
    fun getTags()
    fun putARecipe(recipe: Recipe)
    fun putNewTags(tags: List<String>)
}