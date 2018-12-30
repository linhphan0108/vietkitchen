package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.os.Looper
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.domain.command.RequestTagsCommand
import com.example.linh.vietkitchen.extension.toListOfStringOfKey
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.mapper.TagMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.model.SearchItem
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class HomeActivityPresenter(private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                            private val categoryMapper: CategoryMapper = CategoryMapper())
    : BasePresenter<HomeActivityContractView>() , HomeActivityContractPresenter {

    override fun attachView(view: HomeActivityContractView) {
        super.attachView(view)
        EventBus.getDefault().register(this)
    }

    override fun detachView() {
        EventBus.getDefault().unregister(this)
        super.detachView()
    }

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
                EventBus.getDefault().post(it)
            }
        },{e->
            Timber.e(e)
            viewContract?.onRequestCategoriesFailed(getStringRes(R.string.message_error))
        }, false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventBus(event: List<DrawerNavGroupItem>) {
        viewContract?.onRequestCategoriesSuccess(event)
        Timber.d("onMessageEventBus(): DrawerNavGroupItem received")
    }
}