package com.example.linh.vietkitchen.data.response

data class PagingResponse<T>(val data: T?, val isEnd: Boolean, val lastId: String?)