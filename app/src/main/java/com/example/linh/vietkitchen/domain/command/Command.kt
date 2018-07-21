package com.example.linh.vietkitchen.domain.command

import io.reactivex.Flowable

interface Command<out T> {
    fun execute(): Flowable<out T>
}