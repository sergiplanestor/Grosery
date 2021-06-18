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
import kotlinx.coroutines.channels.ProducerScope
import kotlin.reflect.KClass

// -------------------------------------------------------------------------------------------------
// DatabaseReference / DataSnapshot
// -------------------------------------------------------------------------------------------------

inline fun <T> DatabaseReference.offerOnSingleValue(
    producerScope: ProducerScope<T>,
    crossinline onReceivedBlock: (DataSnapshot) -> T?
) = offerOnSingleValueOrThrow(
    producerScope,
    FlowEmissionBehavior.EMIT_ALL,
    onReceivedBlock,
    { false }
)

inline fun <T> DatabaseReference.offerOnSingleValueOrThrow(
    producerScope: ProducerScope<T>,
    behavior: FlowEmissionBehavior,
    crossinline onReceivedBlock: (DataSnapshot) -> T?,
    crossinline onErrorBlock: (DatabaseError) -> Boolean = { _ -> false }
) {
    addListenerForSingleValueEvent(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            try {
                val data = onReceivedBlock.invoke(snapshot)
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
        }

        override fun onCancelled(error: DatabaseError) {
            if (!onErrorBlock.invoke(error) && behavior.isEmitOnCancelled) {
                throw error.toException()
            }
        }
    })
}

inline fun <reified T: Any> DatabaseReference.pushAsJson(
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

fun Task<*>.offerOnCompletedOrThrow(producerScope: ProducerScope<Boolean>) {
    addOnCompleteListener {
        if (!it.isSuccessful && it.exception != null) {
            // TODO: Check if this exception is being caught by [BaseRepositoryImpl]!
            throw it.exception!!
        } else {
            producerScope.offer(it.isSuccessful)
        }
    }
}