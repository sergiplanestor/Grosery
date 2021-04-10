package com.revolhope.data.common.extensions

import com.google.android.gms.tasks.Task
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


suspend inline fun <T> launchFirebaseCall(crossinline firebaseAction: (Continuation<T>) -> Unit): T =
    suspendCoroutine { continuation -> firebaseAction.invoke(continuation) }


fun Task<*>.addIsSuccessListener(continuation: Continuation<Boolean>) {
    addOnCompleteListener {
        if (!it.isSuccessful && it.exception != null) {
            continuation.resumeWithException(it.exception!!)
        } else {
            continuation.resume(it.isSuccessful)
        }
    }
}

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
