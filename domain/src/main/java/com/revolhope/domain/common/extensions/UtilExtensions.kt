package com.revolhope.domain.common.extensions

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import java.util.UUID

fun randomId() = UUID.randomUUID().toString().replace("-", "")

inline fun delay(
    duration: Long,
    isActionOnMain: Boolean = true,
    crossinline action: () -> Unit
) {
    Handler(
        if (isActionOnMain) {
            Looper.getMainLooper()
        } else {
            Looper.myLooper() ?: Looper.getMainLooper()
        }
    ).postDelayed(delayInMillis = duration, action = action)
}

inline fun <reified T> T?.withDefault(defaultValue: T): T = this ?: defaultValue

inline fun <reified T> T?.withDefault(crossinline block: () -> T): T = this ?: block()