package com.example.linh.vietkitchen.ui.screen.home

import com.example.linh.vietkitchen.ui.model.Recipe
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface BaseHomeContractView : BaseViewContract{
    fun onLikeEventObserve(recipe: Recipe)
    fun onUnlikeEventObserve(recipe: Recipe)
}

interface BaseHomeContractPresenter<T: BaseHomeContractView> : BasePresenterContract<T>{
    fun likeRecipe(recipe: Recipe)
    fun unLikeRecipe(recipe: Recipe)
    fun emitLike(recipe: Recipe)
    fun emitUnLike(recipe: Recipe)
}