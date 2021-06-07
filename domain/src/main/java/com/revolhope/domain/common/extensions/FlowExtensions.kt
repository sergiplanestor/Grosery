package com.revolhope.domain.common.extensions

import com.revolhope.domain.common.model.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

// tODO: Remove file if useless

suspend fun <T> Flow<State<T>>.onCollectState(
    onSuccess: suspend (T) -> Unit,
    onLoading: suspend () -> Unit = {},
    onError: suspend (State.Error) -> Unit = { _ -> }
) {
    collectState(
        blockOnSuccess = onSuccess,
        blockOnLoading = onLoading,
        blockOnError = onError
    )
}

suspend inline fun <T> Flow<State<T>>.collectState(
    crossinline blockOnSuccess: suspend (T) -> Unit,
    crossinline blockOnLoading: suspend () -> Unit,
    crossinline blockOnError: suspend (State.Error) -> Unit
) {
    this.collectLatest { state ->
        when (state) {
            is State.Error -> blockOnError.invoke(state)
            State.Loading -> blockOnLoading.invoke()
            is State.Success -> blockOnSuccess.invoke(state.data)
        }
    }
}
