/*
 * Copyright (C) 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.linh.vietkitchen.domain.provider

import androidx.lifecycle.LiveData
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Transformations
import com.example.linh.vietkitchen.data.response.*
import com.example.linh.vietkitchen.vo.Resource

/**
 * A generic class that can provide a resource backed by both the sqlite database and the network.
 *
 *
 * You can read more about it in the [Architecture
 * Guide](https://developer.android.com/arch).
 * @param <ResultType>
 * @param <RequestType>
</RequestType></ResultType> */
abstract class NetworkBoundResource<ResultType, RequestType>
@MainThread constructor() {

    private val result = MediatorLiveData<Resource<ResultType>>()

    fun execute(): LiveData<Resource<ResultType>> {
        setValue(Resource.loading(null))
        @Suppress("LeakingThis")
        val dbSource = callDb()
        result.addSource(dbSource) { data ->
            result.removeSource(dbSource)
            if (shouldFetch(data)) {
                callNetwork(dbSource)
            } else {
                result.addSource(dbSource) { newData ->
                    setValue(Resource.success(newData))
                }
            }
        }

        return asLiveData()
    }

    @MainThread
    protected fun setValue(newValue: Resource<ResultType>) {
        if (result.value != newValue) {
            result.postValue(newValue)
        }
    }

    private fun callNetwork(dbSource: LiveData<ResultType>) {
        val apiResponse = createCall()
        // we re-attach dbSource as a new source, it will dispatch its latest value quickly
        result.addSource(dbSource) { newData ->
            setValue(Resource.loading(newData))
        }
        result.addSource(apiResponse) { response ->
            result.removeSource(apiResponse)
            result.removeSource(dbSource)
            when (response) {
                is ApiSuccessResponse -> {
                    val result = saveCallResult(processResponse(response))
                    setValue(Resource.success(result))
                    // we specially request a new live data,
                    // otherwise we will get immediately last cached value,
                    // which may not be updated with latest results received from network.
//                setValue(Resource.success(callDb().value)) todo after implement local db this line should be commented out
                }
                is ApiEmptyResponse -> {
                    // reload from disk whatever we had
                    setValue(Resource.success(callDb().value))
                }
                is ApiErrorResponse -> {
                    onFetchFailed()
                    setValue(Resource.error(response.errorMessage))
                }
            }
        }
    }

    protected open fun onFetchFailed() {}

    fun asLiveData() = result as LiveData<Resource<ResultType>>

    @WorkerThread
    protected open fun processResponse(response: ApiSuccessResponse<RequestType>) = response.data

    @WorkerThread
    protected abstract fun saveCallResult(item: RequestType) : ResultType

    @MainThread
    protected open fun shouldFetch(data: ResultType?) = data == null

    @MainThread
    protected abstract fun callDb(): LiveData<ResultType>

    @MainThread
    protected abstract fun createCall(): LiveData<ApiResponse<RequestType>>
}
