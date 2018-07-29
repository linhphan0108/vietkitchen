package com.example.linh.vietkitchen.ui.home.homeFragment

import com.example.linh.vietkitchen.domain.model.Food
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface HomeFragmentContractView : BaseViewContract {
    fun onFoodsRequestSuccess(foods: List<Food>)
    fun onFoodsRequestFailed(msg: String)
}

interface HomeFragmentContractPresenter : BasePresenterContract<HomeFragmentContractView> {
    fun requestFoods()
    fun refreshFoods()
}