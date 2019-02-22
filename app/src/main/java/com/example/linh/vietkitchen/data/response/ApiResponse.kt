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

package com.example.linh.vietkitchen.data.response

/**
 * Common class used by API responses.
 * @param <T> the type of the response object
</T> */
@Suppress("unused") // T is used in extending classes
sealed class ApiResponse<T> {
    companion object {
        fun <T> createError(error: Throwable): ApiErrorResponse<T> {
            return ApiErrorResponse(error.message ?: "unknown error")
        }

        fun <T> createError(error: String?): ApiErrorResponse<T> {
            return ApiErrorResponse(error ?: "unknown error")
        }

        fun <T> createSuccess(response: T?): ApiResponse<T> {
            return if (response == null) {
                createEmpty()
            } else {
                ApiSuccessResponse(response)
            }
        }

        fun <T> createEmpty(): ApiResponse<T> {
            return ApiEmptyResponse()
        }
    }
}

/**
 * separate class for HTTP 204 responses so that we can make ApiSuccessResponse's data non-null.
 */
class ApiEmptyResponse<T> : ApiResponse<T>()

data class ApiSuccessResponse<T> internal constructor(val data: T) : ApiResponse<T>()

data class ApiErrorResponse<T> internal constructor(val errorMessage: String) : ApiResponse<T>()
