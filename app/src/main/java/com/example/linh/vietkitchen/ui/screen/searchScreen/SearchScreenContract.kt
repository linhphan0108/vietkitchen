package com.example.linh.vietkitchen.ui.screen.searchScreen

import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract


interface SearchContractView : BaseViewContract {
    fun onStartLoadMore()
    fun onFoodsRequestSuccess(recipes: List<Recipe>)
    fun onFoodsRequestFailed(msg: String)
    fun onRefreshRecipe()
    fun onLoadMoreSuccess(recipes: List<Recipe>)
    fun onLoadMoreFailed()
    fun onLoadMoreReachEndRecord()
}

interface SearchScreenContractPresenter : BasePresenterContract<SearchContractView> {
    fun requestRecipesByTag(tag: String)
    fun searchRecipesBy(item: SearchItem)
}