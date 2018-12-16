package com.example.linh.vietkitchen.ui.screen.home.homeFragment

import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeContractPresenter
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeContractView

interface HomeFragmentContractView : BaseHomeContractView {
    fun onStartLoadMore()
    fun onFoodsRequestSuccess(recipes: List<Recipe>)
    fun onFoodsRequestFailed(msg: String)
    fun onRefreshRecipe()
    fun onLoadMoreSuccess(recipes: List<Recipe>)
    fun onLoadMoreFailed()
    fun onLoadMoreReachEndRecord()
    fun onDeleteRecipeSuccess(adapterPosition: Int)
    fun onDeleteRecipeFailed(msg: String)
}

interface HomeFragmentContractPresenter : BaseHomeContractPresenter<HomeFragmentContractView> {
    fun requestFoods(category: String? = null)
    fun refreshRecipes(category: String? = null)
    fun loadMoreRecipe()
    fun deleteRecipe(recipe: Recipe, adapterPosition: Int)
}