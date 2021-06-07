package com.revolhope.data.common.extensions

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.revolhope.data.common.crypto.decrypt
import com.revolhope.data.common.crypto.encrypt
import com.revolhope.data.common.exceptions.FirebaseInnerException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass

enum class ContinuationBehavior {
    RESUME_ON_SUCCESS,
    RESUME_WITH_EXCEPTION_ON_ERROR,
    RESUME_ON_SUCCESS_AND_ERROR,
    RESUME_WITH_EXCEPTION_ON_CANCELLED,
    RESUME_ON_SUCCESS_AND_CANCELLED,
    RESUME_WITH_EXCEPTION_ANY_CASE,
    RESUME_ALL,
    RESUME_NEVER;

    val isContinueOnSuccess: Boolean
        get() = this != RESUME_NEVER &&
                this == RESUME_ALL ||
                this == RESUME_ON_SUCCESS_AND_ERROR ||
                this == RESUME_ON_SUCCESS_AND_CANCELLED ||
                this == RESUME_ON_SUCCESS

    val isContinueOnError: Boolean
        get() = this != RESUME_NEVER &&
                this == RESUME_ALL ||
                this == RESUME_ON_SUCCESS_AND_ERROR ||
                this == RESUME_WITH_EXCEPTION_ANY_CASE ||
                this == RESUME_WITH_EXCEPTION_ON_ERROR

    val isContinueOnCancelled: Boolean
        get() = this != RESUME_NEVER &&
                this == RESUME_ALL ||
                this == RESUME_ON_SUCCESS_AND_CANCELLED ||
                this == RESUME_WITH_EXCEPTION_ANY_CASE ||
                this == RESUME_WITH_EXCEPTION_ON_CANCELLED
}

suspend inline fun <T> runOnSuspended(crossinline block: Continuation<T>.() -> Unit): T =
    suspendCoroutine { block.invoke(it) }

suspend inline fun <T> DatabaseReference.fetchOnSuspended(
    behavior: ContinuationBehavior = ContinuationBehavior.RESUME_ALL,
    crossinline onReceivedBlock: (DataSnapshot) -> T?
): T = fetchOnSuspended(behavior, onReceivedBlock, { false })

suspend inline fun <T> DatabaseReference.fetchOnSuspended(
    behavior: ContinuationBehavior = ContinuationBehavior.RESUME_ALL,
    crossinline onReceivedBlock: (DataSnapshot) -> T?,
    crossinline onErrorBlock: (DatabaseError) -> Boolean = { _ -> false }
): T =
    runOnSuspended {
        addOnSingleEventListener(
            continuation = this,
            behavior = behavior,
            onReceivedBlock = onReceivedBlock,
            onErrorBlock = onErrorBlock
        )
    }

inline fun <T> DatabaseReference.addOnSingleEventListener(
    continuation: Continuation<T>,
    behavior: ContinuationBehavior,
    crossinline onReceivedBlock: (DataSnapshot) -> T?,
    crossinline onErrorBlock: (DatabaseError) -> Boolean = { _ -> false }
) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val data = onReceivedBlock.invoke(snapshot)
                if (data != null && behavior.isContinueOnSuccess) {
                    continuation.resume(data)
                } else if (data == null && behavior.isContinueOnError) {
                    continuation.resumeWithException(
                        FirebaseInnerException.NullDataReceived.default.toThrowable()
                    )
                }
            } catch (t: Throwable) {
                if (behavior.isContinueOnError) continuation.resumeWithException(t)
            }
        }

        override fun onCancelled(error: DatabaseError) {
            if (!onErrorBlock.invoke(error) && behavior.isContinueOnCancelled) {
                continuation.resumeWithException(error.toException())
            }
        }
    })
}

fun Task<*>.addResumeOnCompleteListener(continuation: Continuation<Boolean>) {
    addOnCompleteListener {
        if (!it.isSuccessful && it.exception != null) {
            continuation.resumeWithException(it.exception!!)
        } else {
            continuation.resume(it.isSuccessful)
        }
    }
}


fun <T : Any> DataSnapshot.valueJSON(clazz: KClass<T>, isEncrypted: Boolean = true): T? =
    getValue(String::class.java)?.let { if (isEncrypted) it.decrypt(clazz) else it.fromJsonTo(clazz) }

inline fun <reified T : Any> DatabaseReference.valueJSON(
    data: T,
    isEncrypt: Boolean = true
): Task<Void> =
    setValue(if (isEncrypt) data.encrypt else data.asJson())

// TODO: Remove if not using
@Suppress("UNCHECKED_CAST")
inline fun <R, T> Task<T>.handleResponse(
    continuation: Continuation<R?>,
    crossinline onSuccess: ((T?) -> R?) = { taskResult -> taskResult as? R },
    crossinline onError: ((T?) -> R?) = { taskResult -> taskResult as? R }
) {
    addOnCompleteListener {
        if (!it.isSuccessful && it.exception != null) {
            continuation.resumeWithException(it.exception!!)
        } else if (it.isSuccessful) {
            continuation.resume(onSuccess.invoke(it.result))
        } else {
            continuation.resume(onError.invoke(it.result))
        }
    }
}
