package com.example.linh.vietkitchen.ui.home.homeFragmentonRefresh

import com.example.linh.vietkitchen.domain.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface HomeFragmentContractView : BaseViewContract {
    fun onFoodsRequestSuccess(recipes: List<Recipe>)
    fun onFoodsRequestFailed(msg: String)
    fun onLoadingMore()
    fun onRefreshRecipe()
    fun onLoadMoreSuccess(recipes: List<Recipe>)
    fun onLoadMoreFailed()
    fun onLoadMoreReachEndRecord()
}

interface HomeFragmentContractPresenter : BasePresenterContract<HomeFragmentContractView> {
    fun requestFoods(category: String? = null)
    fun refreshFoods(category: String? = null)
    fun loadMoreRecipe()
    fun putARecipe()
}