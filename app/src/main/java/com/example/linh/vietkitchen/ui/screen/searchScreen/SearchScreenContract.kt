package com.example.linh.vietkitchen.ui.screen.searchScreen

import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract


interface SearchContractView : BaseViewContract {
    fun onStartLoadMore()
    fun onRecipesRequestSuccess(recipes: List<Recipe>)
    fun onRecipesRequestFailed(msg: String)
    fun onRefreshRecipe()
    fun onLoadMoreSuccess(recipes: List<Recipe>)
    fun onLoadMoreFailed()
    fun onLoadMoreReachEndRecord()
    fun onGetTagsSuccess(tags: List<SearchItem>)
    fun onGetTagsFailed(message: String?)
    fun onFilterListTag(filteredTags: List<SearchItem>)
    fun onRequestCategoriesSuccess(items: List<DrawerNavGroupItem>)
    fun onRequestCategoriesFailed(message: String)
}

interface SearchScreenContractPresenter : BasePresenterContract<SearchContractView> {
    fun requestTags()
    fun getListTags(): List<SearchItem>
    fun filterSearchSuggestions(query: String?)
    fun searchRecipesBy(item: SearchItem)
    fun requestCategory()
}