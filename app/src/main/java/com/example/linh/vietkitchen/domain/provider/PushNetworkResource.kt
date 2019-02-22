package com.example.linh.vietkitchen.domain.provider

abstract class PushNetworkResource<ResultType, RequestType> : NetworkBoundResource<ResultType, RequestType>() {
    override fun shouldFetch(data: ResultType?) = true
}