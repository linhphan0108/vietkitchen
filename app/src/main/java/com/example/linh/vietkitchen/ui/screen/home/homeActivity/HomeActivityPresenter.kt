package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.os.Looper
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import org.greenrobot.eventbus.EventBus
import timber.log.Timber

class HomeActivityPresenter(private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                            private val categoryMapper: CategoryMapper = CategoryMapper())
    : BasePresenter<HomeActivityContractView>() , HomeActivityContractPresenter {

    override fun requestCategory() {
        launchDataLoad({
            val categories = withIoContext {
                Timber.d("on launchDataLoad: ${Looper.myLooper() == Looper.getMainLooper()}")
                val response = requestCategoryCommand.execute()
                response.data?.let {listCategories->
                    return@withIoContext categoryMapper.convertToUI(listCategories).toMutableList()
                }
            }
            categories?.let {
                viewContract?.onRequestCategoriesSuccess(it)
                EventBus.getDefault().post(it)
            }
        },{e->
            viewContract?.onRequestCategoriesFailed(getStringRes(R.string.message_error))
        }, false)
    }
}