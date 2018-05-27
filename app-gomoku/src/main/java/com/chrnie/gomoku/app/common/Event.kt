package com.chrnie.gomoku.app.common

import android.support.annotation.GuardedBy

class Event<T>(val data: T) {

    @GuardedBy("this")
    @Volatile
    var isConsume = false
        private set

    fun consume(): T? = synchronized(this) {
        if (isConsume) return@synchronized null

        isConsume = true
        return data
    }
}