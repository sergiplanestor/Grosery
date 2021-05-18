package com.revolhope.data.common.extensions

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.revolhope.data.common.crypto.decrypt
import com.revolhope.data.common.crypto.encrypt
import java.lang.RuntimeException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KClass


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

inline fun <T> DatabaseReference.addSingleEventListener(
    continuation: Continuation<T>,
    crossinline onReceived: (DataSnapshot) -> T?,
    crossinline onError: (DatabaseError) -> Boolean = { _ -> false }
) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            onReceived.invoke(snapshot)?.let(continuation::resume)
                ?: continuation.resumeWithException(RuntimeException("FirebaseDataSourceImpl: Unable to cast firebase data"))
        }

        override fun onCancelled(error: DatabaseError) {
            if (!onError.invoke(error)) continuation.resumeWithException(error.toException())
        }
    })
}

fun <T : Any> DataSnapshot.valueJSON(clazz: KClass<T>, isEncrypted: Boolean = true): T? =
    getValue(String::class.java)?.let { if (isEncrypted) it.decrypt(clazz) else it.fromJSON(clazz) }

inline fun <reified T : Any> DatabaseReference.valueJSON(data: T, isEncrypt: Boolean = true): Task<Void> =
    setValue(if (isEncrypt) data.encrypt else data.json )

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
