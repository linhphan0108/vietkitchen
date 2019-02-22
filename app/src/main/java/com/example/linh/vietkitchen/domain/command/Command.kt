package com.example.linh.vietkitchen.domain.command

import android.content.Context
import android.net.ConnectivityManager
import androidx.lifecycle.LiveData
import com.example.linh.vietkitchen.exception.NoInternetConnection
import com.example.linh.vietkitchen.vo.Resource

interface CommandCoroutines<T>{

    fun execute(): LiveData<Resource<T>>

    fun execute(context: Context): LiveData<Resource<T>>

    fun isInternetOn(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetworkInfo = connectivityManager.activeNetworkInfo
        return if (activeNetworkInfo != null && activeNetworkInfo.isConnected){
            true
        }else{
            throw NoInternetConnection()
        }
    }
}