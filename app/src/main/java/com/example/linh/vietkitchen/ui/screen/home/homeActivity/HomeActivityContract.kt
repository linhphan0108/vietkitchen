package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface HomeActivityContractView : BaseViewContract {
    fun onRequestCategoriesSuccess(items: List<DrawerNavGroupItem>)
    fun onRequestCategoriesFailed(message: String)
}


interface HomeActivityContractPresenter : BasePresenterContract<HomeActivityContractView> {
    fun requestCategory()
}