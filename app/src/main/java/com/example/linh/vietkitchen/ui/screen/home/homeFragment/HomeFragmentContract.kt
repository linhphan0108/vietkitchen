package com.example.linh.vietkitchen.ui.home.homeFragmentonRefresh

import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeContractPresenter
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeContractView

interface HomeFragmentContractView : BaseHomeContractView {
    fun onFoodsRequestSuccess(recipes: List<Recipe>)
    fun onFoodsRequestFailed(msg: String)
    fun onLoadingMore()
    fun onRefreshRecipe()
    fun onLoadMoreSuccess(recipes: List<Recipe>)
    fun onLoadMoreFailed()
    fun onLoadMoreReachEndRecord()
}

interface HomeFragmentContractPresenter : BaseHomeContractPresenter<HomeFragmentContractView> {
    fun requestFoods(category: String? = null)
    fun refreshFoods(category: String? = null)
    fun loadMoreRecipe()
    fun putARecipe()
}