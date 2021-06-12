package com.revolhope.presentation.library.extensions

import android.content.res.ColorStateList
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import androidx.annotation.ColorInt
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.revolhope.domain.common.model.State
import com.revolhope.presentation.library.base.BaseViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
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

suspend inline fun <T> Flow<State<T>>.collectOnMainContext(crossinline action: suspend (value: State<T>) -> Unit) {
    this.collect { state ->
        if (coroutineScope { coroutineContext } != Dispatchers.Main) {
            withContext(Dispatchers.Main) {
                action.invoke(state)
            }
        } else {
            action.invoke(state)
        }
    }
}

inline fun <T> LifecycleOwner.observe(data: LiveData<T>, crossinline action: (T) -> Unit) =
    data.observe(this, { action.invoke(it) })


fun Drawable?.applyTint(@ColorInt colorInt: Int) =
    this?.setTintList(ColorStateList.valueOf(colorInt))

data class TaskCallbackWrapper<T>(
    val onTaskSuccess: (data: T) -> Unit,
    val onTaskLoading: (() -> Unit)? = null,
    val onTaskError: ((isResource: Boolean, message: String?) -> Unit)? = null,
)

data class FlowTaskWrapper<T>(
    val task: suspend () -> Flow<State<T>>,
    val messageWhileLoading: String? = null,
    val callbackWrapper: TaskCallbackWrapper<T>
)

fun <T> BaseViewModel.flowOf(
    dispatcher: CoroutineContext = Dispatchers.IO,
    flowTaskWrapper: FlowTaskWrapper<T>
): Job = viewModelScope.launch {
    withContext(dispatcher) {
        flowTaskWrapper.task.invoke().collectOnMainContext {
            handleState(
                state = it,
                onSuccess = flowTaskWrapper.callbackWrapper.onTaskSuccess,
                onLoading = flowTaskWrapper.callbackWrapper.onTaskLoading,
                onLoadingFeedbackMessage = flowTaskWrapper.messageWhileLoading,
                onError = flowTaskWrapper.callbackWrapper.onTaskError
            )
        }
    }
}

fun <T> BaseViewModel.flowOf(
    loadingMessage: String? = null,
    task: suspend () -> Flow<State<T>>,
    onTaskSuccess: (data: T) -> Unit,
) = flowOf(
    flowTaskWrapper = FlowTaskWrapper(
        task = task,
        messageWhileLoading = loadingMessage,
        callbackWrapper = TaskCallbackWrapper(
            onTaskSuccess = onTaskSuccess
        )
    )
)

inline fun <T> CoroutineScope.launchAsync(
    dispatcher: CoroutineContext = Dispatchers.IO,
    crossinline asyncTask: suspend () -> T,
    crossinline onTaskCompleted: T.() -> Unit = {}
) = launch {
    withContext(dispatcher) {
        asyncTask.invoke()
    }.also(onTaskCompleted)
}
