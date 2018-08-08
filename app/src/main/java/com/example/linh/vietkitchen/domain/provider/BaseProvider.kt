package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.extension.firstResult
import com.example.linh.vietkitchen.extension.lastResult

abstract class BaseProvider<T>(private val sources: List<T>) {

    protected fun <H : Any> requestToSources(f: (T) -> H?): H = sources.firstResult { f(it) }
    protected fun <H : Any> putToSources(f: (T) -> H?): H = sources.lastResult{ f(it) }
}