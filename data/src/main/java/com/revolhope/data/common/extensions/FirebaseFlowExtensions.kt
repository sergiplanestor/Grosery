package com.revolhope.data.common.extensions

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.revolhope.data.common.crypto.decrypt
import com.revolhope.data.common.crypto.encrypt
import com.revolhope.data.common.exceptions.FirebaseInnerException
import com.revolhope.domain.common.extensions.FlowEmissionBehavior
import kotlin.reflect.KClass
import kotlinx.coroutines.channels.ProducerScope

// -------------------------------------------------------------------------------------------------
// DatabaseReference / DataSnapshot
// -------------------------------------------------------------------------------------------------

inline fun <T> DatabaseReference.offerOnValue(
    producerScope: ProducerScope<T>,
    isSingleShot: Boolean = true,
    crossinline onReceivedBlock: (DataSnapshot) -> T?
) = offerValueOrThrow(
    producerScope = producerScope,
    behavior = FlowEmissionBehavior.EMIT_ALL,
    isSingleShot = isSingleShot,
    onReceived = onReceivedBlock,
    onFailure = { false }
)

inline fun <T> DatabaseReference.offerValueOrThrow(
    producerScope: ProducerScope<T>,
    behavior: FlowEmissionBehavior,
    isSingleShot: Boolean = true,
    crossinline onReceived: (DataSnapshot) -> T?,
    crossinline onFailure: (DatabaseError) -> Boolean = { _ -> false }
) {
    addValueEvent(
        isSingleShot = isSingleShot,
        onReceived = { snapshot ->
            try {
                val data = onReceived.invoke(snapshot)
                if (data != null && behavior.isEmitOnSuccess) {
                    producerScope.offer(data)
                } else if (data == null && behavior.isEmitOnError) {
                    FirebaseInnerException.NullDataReceived.default.`throw`()
                }
            } catch (t: Throwable) {
                if (behavior.isEmitOnError) {
                    throw t
                }
            }
        },
        onFailure = { error ->
            if (!onFailure.invoke(error) && behavior.isEmitOnCancelled) {
                throw error.toException()
            }
        }
    )
}

inline fun DatabaseReference.addValueEvent(
    isSingleShot: Boolean = true,
    crossinline onReceived: (DataSnapshot) -> Unit,
    crossinline onFailure: (DatabaseError) -> Unit
) {
    object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            onReceived.invoke(snapshot)
        }

        override fun onCancelled(error: DatabaseError) {
            onFailure.invoke(error)
        }
    }.run {
        if (isSingleShot) {
            addListenerForSingleValueEvent(this)
        } else {
            addValueEventListener(this)
        }
    }
}

inline fun <reified T : Any> DatabaseReference.pushAsJson(
    value: T,
    isEncrypt: Boolean = true
): Task<Void> =
    push().insertAsJson(value, isEncrypt)

inline fun <reified T : Any> DatabaseReference.insertAsJson(
    value: T,
    isEncrypt: Boolean = true
): Task<Void> =
    setValue(if (isEncrypt) value.encrypt else value.asJson())

fun <T : Any> DataSnapshot.fetchJsonTo(clazz: KClass<T>, isEncrypted: Boolean = true): T? =
    getValue(String::class.java)?.let { if (isEncrypted) it.decrypt(clazz) else it.fromJsonTo(clazz) }

// -------------------------------------------------------------------------------------------------
// Task
// -------------------------------------------------------------------------------------------------

fun Task<*>.addOnCompleteListener(
    onCompleted: (Boolean) -> Unit,
    onFailure: (Throwable) -> Unit = { throwable -> throw throwable }
) {
    addOnCompleteListener {  result ->
        if (!result.isSuccessful && result.exception != null) {
            onFailure.invoke(result.exception!!)
        } else {
            onCompleted.invoke(result.isSuccessful)
        }
    }
}

fun Task<*>.offerOnCompleted(
    producerScope: ProducerScope<Boolean>,
    onFailure: (Throwable) -> Unit = { throwable -> throw throwable }
) {
    addOnCompleteListener(
        onCompleted = producerScope::offer,
        onFailure = onFailure
    )
}