package com.example.linh.vietkitchen.admin.ui.screen.admin

import android.net.Uri
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
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
    fun showProgressDialog()
    fun updateProgress(totalFiles: Int, counter: Int, progress: Int)
    fun updateMessage(msg: String)
}

interface AdminContractPresenter: BasePresenterContract<AdminContractView>{
    fun setCategoriesList(categories: List<DrawerNavGroupItem>)
    fun openCategoryDialogChecker()
    fun preview(recipe: Recipe)
    fun getTags()
    fun putARecipe(recipe: Recipe, listImagesUri: MutableList<Uri>)
}