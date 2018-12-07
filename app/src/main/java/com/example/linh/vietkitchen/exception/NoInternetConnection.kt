package com.example.linh.vietkitchen.exception

import android.accounts.NetworkErrorException

class NoInternetConnection : NetworkErrorException("no internet connection") {
}