package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.extension.firstResult
import com.example.linh.vietkitchen.extension.lastResult

abstract class BaseProvider<T>(private val sources: List<T>) {

    protected suspend fun <H : Any> requestFirstSources(f: suspend (T) -> H?): H = sources.firstResult { f(it) }
    protected suspend fun <H : Any> requestAllSources(f: suspend (T) -> H?): H = sources.lastResult{ f(it) }
}