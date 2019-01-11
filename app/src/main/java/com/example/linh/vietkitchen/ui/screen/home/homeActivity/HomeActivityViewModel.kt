package com.example.linh.vietkitchen.ui.screen.home.homeActivity

import android.app.Application
import androidx.lifecycle.MutableLiveData
import android.os.Looper
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.ui.baseMVVM.BaseViewModel
import com.example.linh.vietkitchen.ui.baseMVVM.Status
import com.example.linh.vietkitchen.ui.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import timber.log.Timber

class HomeActivityViewModel(application: Application,
                            private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                            private val categoryMapper: CategoryMapper = CategoryMapper())
    : BaseViewModel(application) {

    internal val listNav: MutableLiveData<List<DrawerNavGroupItem>> = MutableLiveData()
    internal var requestNavStatus: MutableLiveData<Int> = MutableLiveData()

    init {
        requestNavStatus.value = Status.NORMAL
        EventBus.getDefault().register(this)
    }

    override fun onCleared() {
        EventBus.getDefault().unregister(this)
        super.onCleared()
    }


    fun requestCategory() {
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
            requestNavStatus.value = Status.ERROR
        }, false)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEventBus(event: List<DrawerNavGroupItem>) {
        listNav.value = event
        Timber.d("onMessageEventBus(): DrawerNavGroupItem received")
    }
}