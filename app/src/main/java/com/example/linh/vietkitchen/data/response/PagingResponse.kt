package com.example.linh.vietkitchen.data.response

import java.lang.Exception

class PagingResponse<T>(code: Int, data: T?, val isEnd: Boolean, message: String? = null, exception: Exception? = null)
    : Response<T>(code, data, message, exception)