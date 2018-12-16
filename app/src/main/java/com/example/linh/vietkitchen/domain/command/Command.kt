package com.example.linh.vietkitchen.domain.command

import android.content.Context
import android.net.ConnectivityManager
import com.example.linh.vietkitchen.exception.NoInternetConnection

interface CommandCoroutines<out T>{

    suspend fun execute(): T

    suspend fun executeOnTheInternet(context: Context): T

    suspend fun isInternetOn(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return if (activeNetworkInfo != null && activeNetworkInfo.isConnected){
            true
        }else{
            throw NoInternetConnection()
        }
    }
}