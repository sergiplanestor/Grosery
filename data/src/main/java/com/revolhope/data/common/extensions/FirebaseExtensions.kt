package com.revolhope.data.common.extensions

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.revolhope.data.common.exceptions.FirebaseInnerException
import com.revolhope.domain.common.extensions.FlowEmissionBehavior
import com.revolhope.domain.common.extensions.runOnSuspendedOrNull
import kotlinx.coroutines.flow.FlowCollector
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

// -------------------------------------------------------------------------------------------------
// DatabaseReference / DataSnapshot
// -------------------------------------------------------------------------------------------------

suspend inline fun <T> DatabaseReference.fetchOnSuspendedOrDefault(
    default: T,
    behavior: FlowEmissionBehavior = FlowEmissionBehavior.EMIT_ALL,
    crossinline onReceivedBlock: (DataSnapshot) -> T?
): T = fetchOnSuspendedOrDefault(
    default = default,
    behavior = behavior,
    onReceivedBlock = onReceivedBlock,
    onErrorBlock = { false }
)

suspend inline fun <T> DatabaseReference.fetchOnSuspendedOrDefault(
    default: T,
    behavior: FlowEmissionBehavior = FlowEmissionBehavior.EMIT_ALL,
    crossinline onReceivedBlock: (DataSnapshot) -> T?,
    crossinline onErrorBlock: (DatabaseError) -> Boolean = { _ -> false }
): T = fetchOnSuspendedOrNull(behavior, onReceivedBlock, onErrorBlock) ?: default

suspend inline fun <T> DatabaseReference.fetchOnSuspendedOrNull(
    behavior: FlowEmissionBehavior = FlowEmissionBehavior.EMIT_ALL,
    crossinline onReceivedBlock: (DataSnapshot) -> T?
): T? = fetchOnSuspendedOrNull(behavior, onReceivedBlock, { false })

suspend inline fun <T> DatabaseReference.fetchOnSuspendedOrNull(
    behavior: FlowEmissionBehavior = FlowEmissionBehavior.EMIT_ALL,
    crossinline onReceivedBlock: (DataSnapshot) -> T?,
    crossinline onErrorBlock: (DatabaseError) -> Boolean = { _ -> false }
): T? = runOnSuspendedOrNull { cont ->
    addOnSingleEventListener(
        continuation = cont,
        behavior = behavior,
        onReceivedBlock = onReceivedBlock,
        onErrorBlock = onErrorBlock
    )
}

inline fun <T> DatabaseReference.addOnSingleEventListener(
    continuation: Continuation<T>,
    behavior: FlowEmissionBehavior,
    crossinline onReceivedBlock: (DataSnapshot) -> T?,
    crossinline onErrorBlock: (DatabaseError) -> Boolean = { _ -> false }
) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val data = onReceivedBlock.invoke(snapshot)
                if (data != null && behavior.isEmitOnSuccess) {
                    continuation.resume(data)
                } else if (data == null && behavior.isEmitOnError) {
                    continuation.resumeWithException(
                        FirebaseInnerException.NullDataReceived.default.toThrowable()
                    )
                }
            } catch (t: Throwable) {
                if (behavior.isEmitOnError) continuation.resumeWithException(t)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            if (!onErrorBlock.invoke(error) && behavior.isEmitOnCancelled) {
                continuation.resumeWithException(error.toException())
            }
        }
    })
}

// -------------------------------------------------------------------------------------------------
// Task
// -------------------------------------------------------------------------------------------------

fun Task<*>.addResumeOnCompleteListener(continuation: Continuation<Boolean>) {
    addOnCompleteListener {
        if (!it.isSuccessful && it.exception != null) {
            continuation.resumeWithException(it.exception!!)
        } else {
            continuation.resume(it.isSuccessful)
        }
    }
}

suspend fun Task<*>.addEmitOnCompleteListener(collector: FlowCollector<Boolean>) {
    collector.emit(
        suspendCoroutine { cont ->
            addOnCompleteListener {
                if (!it.isSuccessful && it.exception != null) {
                    cont.resumeWithException(it.exception!!)
                } else {
                    cont.resume(it.isSuccessful)
                }
            }
        }
    )
}
