package com.revolhope.data.common.exceptions

import com.revolhope.domain.common.extensions.logError

sealed class FirebaseInnerException(
    open val customTag: String? = null,
    open val message: String,
    open val throwable: Throwable? = null
) {
    companion object {
        private const val DEFAULT_TAG = "FirebaseInnerException"
    }

    abstract val tag: String

    fun toThrowable(): Throwable =
        Throwable(
            message = "$tag: $message",
            cause = throwable
        ).also { logError(tag = tag, message = message, throwable = throwable) }

    fun `throw`() { throw this.toThrowable() }

    data class NullDataReceived(
        override val customTag: String? = null,
        override val message: String,
        override val throwable: Throwable? = null
    ) : FirebaseInnerException(customTag, message, throwable) {

        override val tag: String get() = TAG

        companion object {
            const val TAG = "$DEFAULT_TAG.NullDataReceived"
            const val NULL_FROM_ON_RECEIVED_MSG =
                "Unable to resume suspend fun with data from onReceived(DataSnapshot): T? getting null"

            val default: NullDataReceived get() =
                NullDataReceived(
                    customTag = null,
                    message = NULL_FROM_ON_RECEIVED_MSG,
                    throwable = null
                )
        }
    }

}
