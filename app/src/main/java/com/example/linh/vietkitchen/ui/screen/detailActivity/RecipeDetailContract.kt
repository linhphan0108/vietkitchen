package com.example.linh.vietkitchen.ui.screen.detailActivity

import com.example.linh.vietkitchen.ui.mvpBase.BasePresenterContract
import com.example.linh.vietkitchen.ui.mvpBase.BaseViewContract

interface RecipeDetailViewContract : BaseViewContract{
    /**
     * @param state true is liked otherwise unlike
     */
    fun onLikeChangedSuccess(state: Boolean)
    fun onLikeChangedFailed()
}

interface RecipeDetailPresenterContract: BasePresenterContract<RecipeDetailViewContract>{
    /**
     * @param state true is liked otherwise unlike
     */
    fun onLikeChanged(uid: String, id: String, state: Boolean)
}