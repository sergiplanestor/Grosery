package com.revolhope.domain.common.extensions

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withTimeoutOrNull
import kotlin.coroutines.Continuation
import kotlin.coroutines.suspendCoroutine

// -------------------------------------------------------------------------------------------------
// Continuation / suspended functions
// -------------------------------------------------------------------------------------------------

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

suspend fun <T> runOnSuspendedOrDefault(
    default: T,
    timeout: Long = TIMEOUT_SUSPENDED_BLOCK_DEFAULT,
    block: (Continuation<T>) -> Unit
): T = runOnSuspendedOrNull(timeout, block) ?: default

suspend fun <T> runOnSuspendedOrNull(
    timeout: Long = TIMEOUT_SUSPENDED_BLOCK_DEFAULT,
    block: (Continuation<T>) -> Unit
): T? =
    try {
        if (timeout != TIMEOUT_SUSPENDED_BLOCK_NONE) {
            withTimeoutOrNull(timeout) {
                suspendCoroutine<T> { block.invoke(it) }
            }
        } else {
            suspendCoroutine<T> { block.invoke(it) }
        }
    } catch (e: Throwable) {
        logError<Unit>(tag = "CoroutineExtensions", throwable = e)
        null
    }

suspend fun runOnSuspendedOrFalse(
    timeout: Long = TIMEOUT_SUSPENDED_BLOCK_DEFAULT,
    block: (Continuation<Boolean>) -> Unit
): Boolean = runOnSuspendedOrDefault(default = false, timeout = timeout, block = block)

inline fun <reified T> runOnCallbackFlow(
    crossinline firebaseBlock: suspend ProducerScope<T>.() -> Unit
): Flow<T> =
    callbackFlow {
        try {
            firebaseBlock.invoke(this)
            awaitClose {  }
        } catch (e: Throwable) {
            logError(throwable = e)
            throw e
        }
    }