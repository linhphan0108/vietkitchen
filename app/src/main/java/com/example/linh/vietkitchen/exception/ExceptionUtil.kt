package com.example.linh.vietkitchen.exception

import android.annotation.SuppressLint
import io.reactivex.Completable
import java.util.concurrent.TimeUnit

object ExceptionUtil {
    @SuppressLint("CheckResult")
    fun countToTimeOut(timeOut: Long = 7000): Completable {
        return Completable
                .timer(timeOut, TimeUnit.MILLISECONDS)
    }
}