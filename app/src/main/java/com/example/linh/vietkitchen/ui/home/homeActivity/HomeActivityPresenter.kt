package com.example.linh.vietkitchen.ui.home.homeActivity

import android.os.Looper
import com.example.linh.vietkitchen.R
import com.example.linh.vietkitchen.domain.command.PutRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestCategoryCommand
import com.example.linh.vietkitchen.ui.home.mapper.CategoryMapper
import com.example.linh.vietkitchen.ui.model.DrawerNavGroupItem
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HomeActivityPresenter(private val requestCategoryCommand: RequestCategoryCommand = RequestCategoryCommand(),
                            private val putRecipeCommand: PutRecipeCommand = PutRecipeCommand(),
                            private val categoryMapper: CategoryMapper = CategoryMapper())
    : BasePresenter<HomeActivityContractView>() , HomeActivityContractPresenter {
    override fun requestCategory() {
        requestCategoryCommand.execute()
                .map {
                    Timber.e("on map: ${Looper.myLooper() == Looper.getMainLooper()}")
                    val categories = categoryMapper.convertToUI(it).toMutableList()
                    val groupItemTitleAll = context?.getString(R.string.draw_nav_group_item_all)
                    categories.add(0, DrawerNavGroupItem(groupItemTitleAll!!))
                    categories
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

    override fun putARecipe() {
        compositeDisposable.add(putRecipeCommand.execute()
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    Timber.d("a recipe was put into firebase server")
                })
    }
}