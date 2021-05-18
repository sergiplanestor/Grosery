package com.revolhope.presentation.library.extensions

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.ColorInt
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

inline fun runOn(delay: Long = 0L, isMainThread: Boolean = true, crossinline action: () -> Unit) =
    if (isMainThread) {
        Looper.getMainLooper()
    } else {
        Looper.myLooper() ?: Looper.getMainLooper()
    }.run {
        Handler(this).postDelayed(
            { action.invoke() },
            delay
        )
    }

inline fun <T> LifecycleOwner.observe(data: LiveData<T>, crossinline action: (T) -> Unit) =
    data.observe(this, { action.invoke(it) })


fun Drawable?.applyTint(@ColorInt colorInt: Int) =
    this?.setTintList(ColorStateList.valueOf(colorInt))

inline fun <T> CoroutineScope.launchAsync(
    dispatcher: CoroutineContext = Dispatchers.IO,
    crossinline asyncTask: suspend () -> T,
    crossinline onTaskCompleted: T.() -> Unit = {}
) = launch { withContext(dispatcher) { asyncTask.invoke() }.also(onTaskCompleted) }
