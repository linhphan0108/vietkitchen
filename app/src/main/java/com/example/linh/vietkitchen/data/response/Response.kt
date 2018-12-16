package com.example.linh.vietkitchen.data.response

import java.lang.Exception

open class Response<T>(val code: Int, val data: T?, val message: String? = null, val exception: Exception?= null)