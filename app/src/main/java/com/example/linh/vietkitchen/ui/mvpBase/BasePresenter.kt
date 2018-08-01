package com.example.linh.vietkitchen.ui.mvpBase

import android.content.Context
import io.reactivex.disposables.CompositeDisposable


abstract class BasePresenter<T : BaseViewContract> : BasePresenterContract<T> {

    protected var viewContract: T? = null
    protected var context: Context? = null
    protected val  compositeDisposable : CompositeDisposable by lazy {
        CompositeDisposable()}

    private val isViewAttached: Boolean
        get() = viewContract != null

//    private val mCompositeSubscription = CompositeSubscription()
//    private var subscribeScheduler: Scheduler? = null
//
//    internal val schedulersTransformer: Observable.Transformer = object : Observable.Transformer() {
//        fun call(o: Any): Observable {
//            return (o as Observable).subscribeOn(defaultSubscribeScheduler())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .unsubscribeOn(defaultSubscribeScheduler())
//        }
//    }

    override fun attachView(view: T) {
        this.viewContract = view
        this.context = view.viewContext
    }

    override fun detachView() {
        viewContract = null
        compositeDisposable.clear()
    }

    fun getStringRes(intRes: Int) = this.context?.getString(intRes) ?: ""

//    fun checkViewAttached() {
//        if (!isViewAttached) {
//            throw MvpViewNotAttachedException()
//        }
//    }

//    fun addSubscription(subscription: Subscription) {
//        this.mCompositeSubscription.add(subscription)
//    }

//    class MvpViewNotAttachedException : RuntimeException("Please call Presenter.attachView(MvpView) before" + " requesting data to the Presenter")

    //Reusing Transformers - Singleton
//    fun <T> applySchedulers(): Observable.Transformer<T, T> {
//        return schedulersTransformer as Observable.Transformer<T, T>
//    }

//    fun defaultSubscribeScheduler(): Scheduler {
//        if (subscribeScheduler == null) {
//            subscribeScheduler = Schedulers.io()
//        }
//        return subscribeScheduler
//    }

//    fun handleRetrofitError(error: Throwable) {
//        Timber.e(error)
//        viewContract!!.hideProgress()
//        viewContract!!.loadError(error.message, Constants.RETROFIT_ERROR)
//    }

//    fun checkNotNull(`object`: Any?): Boolean {
//        return `object` != null
//    }
}