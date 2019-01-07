package com.example.linh.vietkitchen.ui.baseMVVM

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import com.example.linh.vietkitchen.extension.removeLast
import com.example.linh.vietkitchen.ui.model.Entity
import com.example.linh.vietkitchen.ui.model.LoadMoreItem
import kotlinx.coroutines.*
import java.lang.Exception

abstract class  BaseViewModel(application: Application) : AndroidViewModel(application) {

    val loadingDialogState: MutableLiveData<Boolean> = MutableLiveData()

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

    override fun onCleared() {
        viewModelJob.cancel()
        super.onCleared()
    }

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
                if (shouldShowProgress) loadingDialogState.value = true
                ioBlock()
            } catch (e: Exception) {
                onError?.invoke(e)
            } finally {
                if (shouldShowProgress) loadingDialogState.value = false
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

    protected fun addLoadMoreItem(list: MutableList<Entity>): Boolean {
        return list.add(LoadMoreItem())
    }

    protected fun removeLoadMoreItem(list: MutableList<Entity>): Boolean{
        val lastPosition = list.size - 1
        val lastItem = list[lastPosition]
        if (lastItem is LoadMoreItem){
            list.removeAt(lastPosition)
            return true
        }
        return false
    }
}