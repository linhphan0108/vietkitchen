package com.example.linh.vietkitchen.domain.command

import io.reactivex.Completable
import io.reactivex.Flowable

interface Command<out T>{
    fun execute(): T
}

interface CommandFollowable<T> : Command<Flowable<out T>>

interface CommandCompletable : Command<Completable>