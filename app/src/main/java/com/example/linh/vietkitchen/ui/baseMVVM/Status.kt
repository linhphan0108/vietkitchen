package com.example.linh.vietkitchen.ui.baseMVVM

object Status {
    const val SUCCESS = 11
    const val NORMAL = -1
    const val LOADING = 1
    const val LOAD_MORE = 2
    const val REFRESH = 3
    const val REACHED_END = 4
    const val HAS_LOGGED_IN = 5
    const val ERROR_NO_INTERNET = 96
    const val LOAD_MORE_ERROR = 97
    const val ERROR_EMPTY = 98
    const val ERROR = 99
}

data class StatusBox<T>(val code: Int = Status.NORMAL, val message: String? = null, val data: T? = null)