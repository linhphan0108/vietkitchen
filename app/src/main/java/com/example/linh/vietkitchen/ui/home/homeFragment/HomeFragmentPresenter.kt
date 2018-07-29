package com.example.linh.vietkitchen.ui.home.homeFragment

import com.example.linh.vietkitchen.domain.command.RequestFoodCommand
import com.example.linh.vietkitchen.ui.mvpBase.BasePresenter
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class HomeFragmentPresenter(private val requestFoodCommand : RequestFoodCommand = RequestFoodCommand())
    : BasePresenter<HomeFragmentContractView>(), HomeFragmentContractPresenter {

    private var  d : CompositeDisposable = CompositeDisposable()

    override fun requestFoods(){
        viewContract?.showProgress()
        d.add(requestFoodCommand.execute()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe ({

                    if (it != null && it.isEmpty()) {
                        viewContract?.onFoodsRequestFailed("Oops! something went wrong")
                    }else{
                        viewContract?.onFoodsRequestSuccess(it)
                    }
                    viewContract?.hideProgress()

                }, {
                    Timber.e(it)
                    viewContract?.hideProgress()
                }))
    }

    override fun refreshFoods() {
        requestFoods()
    }


    override fun detachView() {
        super.detachView()
        d.clear()
    }
}