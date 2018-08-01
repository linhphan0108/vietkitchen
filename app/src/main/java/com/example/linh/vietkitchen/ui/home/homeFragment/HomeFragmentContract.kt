package com.example.linh.vietkitchen.ui.home.homeFragmentonRefresh

import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface HomeFragmentContractView : BaseViewContract {
    fun onFoodsRequestSuccess(foods: List<Food>)
    fun onFoodsRequestFailed(msg: String)
    fun onLoadingMore()
    fun onRefreshRecipe()
    fun onLoadMoreSuccess(recipes: List<Food>)
    fun onLoadMoreFailed()
    fun onLoadMoreReachEndRecord()
}

interface HomeFragmentContractPresenter : BasePresenterContract<HomeFragmentContractView> {
    fun requestFoods(category: String? = null)
    fun refreshFoods(category: String? = null)
    fun loadMoreRecipe()
    fun putARecipe()
}