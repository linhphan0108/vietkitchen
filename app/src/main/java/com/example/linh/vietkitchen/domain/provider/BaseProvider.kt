package com.example.linh.vietkitchen.domain.provider

import com.example.linh.vietkitchen.extension.firstResult

abstract class BaseProvider<T>(private val sources: List<T>) {

    protected fun <H : Any> requestToSources(f: (T) -> H?): H = sources.firstResult { f(it) }
}