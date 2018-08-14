package com.example.linh.vietkitchen.ui.screen.home.favorite

import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeContractPresenter
import com.example.linh.vietkitchen.ui.screen.home.BaseHomeContractView

interface FavoriteContractView : BaseHomeContractView{
    fun onRequestLikedRecipesSuccess(recipes: List<Recipe>)
    fun onRequestLikedRecipesFailed()
    fun onRequestLikedRecipesNoData()
}

interface FavoriteContractPresenter : BaseHomeContractPresenter<FavoriteContractView>{
    fun requestLikedRecipes(uid: String)
    fun requestLikedRecipes(ids: List<String>?)
}