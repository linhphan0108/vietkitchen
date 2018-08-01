package com.example.linh.vietkitchen.ui.home.homeFragment

import com.example.linh.vietkitchen.domain.command.PutRecipeCommand
import com.example.linh.vietkitchen.domain.command.RequestRecipeCommand
import com.example.linh.vietkitchen.ui.home.homeFragmentonRefresh.HomeFragmentContractPresenter
import com.example.linh.vietkitchen.ui.home.homeFragmentonRefresh.HomeFragmentContractView
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HomeFragmentPresenter(private val requestRecipeCommand : RequestRecipeCommand = RequestRecipeCommand(),
                            private val putRecipeCommand: PutRecipeCommand = PutRecipeCommand())
    : BasePresenter<HomeFragmentContractView>(), HomeFragmentContractPresenter {

    private var lastRecipeId: String? = null
    private var isLoadMoreRecipe = false
    private var isFreshRecipe = false
    private var isReachLastRecord = false

    private var requestRecipeDisposable: Disposable? = null

    override fun detachView() {
        super.detachView()
        requestRecipeDisposable?.dispose()
    }

    override fun requestFoods(category: String?){
        if (isLoadMoreRecipe){
            viewContract?.onLoadingMore()
        }else {
            viewContract?.onRefreshRecipe()
        }
        requestRecipeCommand.category = category
        requestRecipeCommand.startAtId = lastRecipeId
        requestRecipeDisposable = requestRecipeCommand.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({
                    if (it != null && it.isEmpty()) {
                        viewContract?.onFoodsRequestFailed("Oops! something went wrong, no data found")
                    }else{
                        if (it.last().id.equals(lastRecipeId)){
                            isReachLastRecord = true
                            isLoadMoreRecipe = false
                            isFreshRecipe = false
                            viewContract?.onLoadMoreReachEndRecord()
                            return@subscribe
                        }
                        lastRecipeId = it.last().id
                        if(isLoadMoreRecipe){
                            viewContract?.onLoadMoreSuccess(it)
                            isLoadMoreRecipe = false
                        }else {
                            viewContract?.onFoodsRequestSuccess(it)
                            isFreshRecipe = false
                        }
                    }
                    viewContract?.hideProgress()

                }, {
                    Timber.e(it)
                    if(isLoadMoreRecipe){
                        viewContract?.onLoadMoreFailed()
                        isLoadMoreRecipe = false
                    }else{
                        viewContract?.onFoodsRequestFailed("Opps some things went wrong. ${it.message}")
                        isFreshRecipe = false
                    }
                })
    }

    override fun loadMoreRecipe() {
        if(isLoadMoreRecipe || isFreshRecipe || isReachLastRecord) return
        isLoadMoreRecipe = true
        requestFoods()
    }

    override fun refreshFoods(category: String?) {
        requestRecipeDisposable?.dispose()
        isFreshRecipe = true
        isLoadMoreRecipe = false
        lastRecipeId = null
        isReachLastRecord = false
        requestFoods(category)
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