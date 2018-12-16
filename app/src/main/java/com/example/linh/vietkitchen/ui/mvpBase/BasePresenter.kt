package com.example.linh.vietkitchen.ui.mvpBase

import android.content.Context
import android.support.v7.app.AppCompatActivity
import kotlinx.coroutines.*
import java.lang.Exception


abstract class BasePresenter<T : BaseViewContract> : BasePresenterContract<T> {
    protected var viewContract: T? = null
    protected var context: Context? = null
    protected var activity: AppCompatActivity? = null
    /**
     * This is the job for all coroutines started by this ViewModel.
     *
     * Cancelling this job will cancel all coroutines started by this ViewModel.
     */
    private val viewModelJob: Job by lazy { Job() }

    /**
     * This is the main scope for all coroutines launched by MainViewModel.
     *
     * Since we pass viewModelJob, you can cancel all coroutines launched by uiScope by calling
     * viewModelJob.cancel()
     */
    private val uiScope: CoroutineScope by lazy { CoroutineScope(Dispatchers.Main + viewModelJob) }

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
        activity = view.viewContext as AppCompatActivity
    }

    override fun detachView() {
        viewContract = null
        viewModelJob.cancel()
    }

    fun getStringRes(intRes: Int) = this.context?.getString(intRes) ?: ""

    /**
     * Helper function to call a data load function with a loading spinner, errors will trigger a
     * snackbar.
     *
     * By marking `ioBlock` as `suspend` this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param ioBlock lambda to actually load data. It is called in the uiScope. Before calling the
     *              lambda the loading spinner will display, after completion or onError the loading
     *              spinner will stop
     */
    fun launchDataLoad(ioBlock: suspend () -> Unit, onError: (suspend (ex: Exception) -> Unit)? = null
                       , shouldShowProgress: Boolean = true): Job {
        return uiScope.launch {
            try {
                if (shouldShowProgress)viewContract?.showProgress()
                ioBlock()
            } catch (e: Exception) {
                onError?.invoke(e)
            } finally {
                if (shouldShowProgress)viewContract?.hideProgress()
            }
        }
    }

    fun launchDataLoad(block: suspend () -> Unit, shouldShowProgress: Boolean = true): Job {
        return launchDataLoad(block, null, shouldShowProgress)
    }

    suspend fun <R> withIoContext(block: suspend () -> R): R{
        return withContext(Dispatchers.IO) {
            block()
        }
    }

    suspend fun <R> withComputationContext(block: suspend () -> R): R{
        return withContext(Dispatchers.Default){
            block()
        }
    }
}