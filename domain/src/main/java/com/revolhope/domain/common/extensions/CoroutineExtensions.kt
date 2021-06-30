package com.revolhope.domain.common.extensions

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// -------------------------------------------------------------------------------------------------
// Flow util functions
// -------------------------------------------------------------------------------------------------

// TODO: Check below
const val TIMEOUT_SUSPENDED_BLOCK_DEFAULT = 30000L // 30s
const val TIMEOUT_SUSPENDED_BLOCK_NONE = -1L // 15s

enum class FlowEmissionBehavior {
    EMIT_ON_SUCCESS,
    EMIT_WITH_EXCEPTION_ON_ERROR,
    EMIT_ON_SUCCESS_AND_ERROR,
    EMIT_WITH_EXCEPTION_ON_CANCELLED,
    EMIT_ON_SUCCESS_AND_CANCELLED,
    EMIT_WITH_EXCEPTION_ANY_CASE,
    EMIT_ALL,
    EMIT_NEVER;

    val isEmitOnSuccess: Boolean
        get() = this != EMIT_NEVER &&
                this == EMIT_ALL ||
                this == EMIT_ON_SUCCESS_AND_ERROR ||
                this == EMIT_ON_SUCCESS_AND_CANCELLED ||
                this == EMIT_ON_SUCCESS

    val isEmitOnError: Boolean
        get() = this != EMIT_NEVER &&
                this == EMIT_ALL ||
                this == EMIT_ON_SUCCESS_AND_ERROR ||
                this == EMIT_WITH_EXCEPTION_ANY_CASE ||
                this == EMIT_WITH_EXCEPTION_ON_ERROR

    val isEmitOnCancelled: Boolean
        get() = this != EMIT_NEVER &&
                this == EMIT_ALL ||
                this == EMIT_ON_SUCCESS_AND_CANCELLED ||
                this == EMIT_WITH_EXCEPTION_ANY_CASE ||
                this == EMIT_WITH_EXCEPTION_ON_CANCELLED
}

suspend inline fun <reified T> launchCallbackFlow(
    crossinline firebaseBlock: suspend ProducerScope<T>.() -> Unit
): Flow<T> = launchCallbackFlow(firebaseBlock = firebaseBlock, closure = {})

suspend inline fun <reified T> launchCallbackFlow(
    crossinline firebaseBlock: suspend ProducerScope<T>.() -> Unit,
    crossinline closure: () -> Unit
): Flow<T> =
    callbackFlow {
        safeSuspendedRun(catch = catchWrapper(isPropagationEnabled = true)) {
            firebaseBlock.invoke(this@callbackFlow)
            awaitClose { closure() }
        }
    }.flowOn(Dispatchers.Main)

inline fun <T> launchOnIO(crossinline block: suspend () -> Unit): Job =
    CoroutineScope(Dispatchers.IO).launch { block() }

suspend inline fun <T> runOnIOContext(crossinline block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.IO) { block() }

suspend inline fun <T> runOnMainContext(crossinline block: suspend CoroutineScope.() -> T): T =
    withContext(Dispatchers.Main) { block() }

inline fun <T, R, S> launchOnIOAndThen(
    crossinline block: suspend () -> T,
    crossinline then: suspend (T) -> R,
    crossinline transformation: (T, R) -> S
): Job =
    CoroutineScope(Dispatchers.IO).launch {
        block().let { transformation(it, then(it)) }
    }
