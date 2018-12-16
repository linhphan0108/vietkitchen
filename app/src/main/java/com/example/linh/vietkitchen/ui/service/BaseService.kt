package com.example.linh.vietkitchen.ui.service

import android.app.Service
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

abstract class BaseService: Service() {
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

    override fun onDestroy() {
        viewModelJob.cancel()
        super.onDestroy()
    }

    //======== inner methods =======================================================================
    /**
     * Helper function to call a data load function with a loading spinner, errors will trigger a
     * snackbar.
     *
     * By marking `block` as `suspend` this creates a suspend lambda which can call suspend
     * functions.
     *
     * @param block lambda to actually load data. It is called in the uiScope. Before calling the
     *              lambda the loading spinner will display, after completion or error the loading
     *              spinner will stop
     */
    fun launchDataLoad(block: suspend () -> Unit): Job {
        return uiScope.launch {
            try {
                block()
            } catch (error: Exception) {
                Timber.e(error)
            } finally {
            }
        }
    }

    suspend fun <R> withIoContext(block: suspend () -> R): R{
        return withContext(Dispatchers.IO) {
            block()
        }
    }
}