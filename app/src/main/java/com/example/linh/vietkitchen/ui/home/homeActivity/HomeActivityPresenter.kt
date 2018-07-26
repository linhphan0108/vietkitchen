package com.example.linh.vietkitchen.ui.home.homeActivity

import android.os.Looper
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.ui.home.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HomeActivityPresenter(private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                            private val categoryMapper: CategoryMapper = CategoryMapper())
    : BasePresenter<HomeActivityContractView>() , HomeActivityContractPresenter {
    override fun requestCategory() {
        requestCategoryCommand.execute()
                .observeOn(Schedulers.io())
                .map {
                    Timber.e("on map: ${Looper.myLooper() == Looper.getMainLooper()}")
                    categoryMapper.convertToUI(it)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    Timber.e("on subscribe: ${Looper.myLooper() == Looper.getMainLooper()}")
                    viewContract?.onRequestCategoriesSuccess(it)
                }, {
                    Timber.e(it)
                    viewContract?.onRequestCategoriesFailed(getStringRes(R.string.message_error))
                })
    }
}