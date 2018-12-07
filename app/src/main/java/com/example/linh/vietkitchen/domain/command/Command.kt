package com.example.linh.vietkitchen.domain.command

import android.content.Context
import android.net.ConnectivityManager
import com.example.linh.vietkitchen.exception.NoInternetConnection
import io.reactivex.Completable
import io.reactivex.Flowable

interface Command<out T>{

    fun execute(): T

    fun executeOnTheInternet(context: Context): T

    fun isInternetOn(context: Context): Flowable<Boolean> {
        return Flowable.fromCallable {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected){
                true
            }else{
                throw NoInternetConnection()
            }
        }
    }
}

interface CommandFollowable<T> : Command<Flowable<out T>>

interface CommandCompletable : Command<Completable>