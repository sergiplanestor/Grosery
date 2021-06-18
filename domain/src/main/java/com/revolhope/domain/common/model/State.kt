package com.revolhope.domain.common.model

sealed class State<out T> {
    object Loading : State<Nothing>()
    data class Success<out T>(val data: T) : State<T>()
    data class Error(
        val errorMessage: ErrorMessage = ErrorMessage(),
        val errorCode: String? = null,
        val throwable: Throwable? = null
    ) : State<Nothing>() {

        constructor(
            message: String? = null,
            messageRes: Int? = null,
            errorCode: String? = null,
            throwable: Throwable? = null
        ) : this(
            errorMessage = ErrorMessage(
                message = message,
                messageRes = messageRes,
            ),
            errorCode = errorCode,
            throwable = throwable
        )

        val isErrorMessageResource: Boolean get() = errorMessage.messageRes != null

        val errorMessageOrEmpty: String
            get() =
                errorMessage.messageRes?.toString() ?: errorMessage.message
                ?: throwable?.message.orEmpty()
    }
}

data class ErrorMessage(
    val message: String? = null,
    val messageRes: Int? = null,
)

// -------------------------------------------------------------------------------------------------
// State extensions
// -------------------------------------------------------------------------------------------------

inline fun <reified T> State<T>.dataAsBooleanStateOrNull(): Boolean? =
    (this as? State.Success)?.let {
        if (it.isBooleanType()) {
            it.data as Boolean
        } else {
            null
        }
    }

inline fun <reified T> State<T>.dataAsBooleanStateOrFalse(): Boolean =
    this.dataAsBooleanStateOrNull() ?: false

inline fun <reified T> State.Success<T>.isBooleanType(): Boolean = this.data is Boolean

inline fun <reified T> State.Success<T>.isBooleanTypeAndTrue(): Boolean =
    this.data as? Boolean != false

fun <T> T.asStateSuccess(): State.Success<T> = State.Success(this)

fun <T : Throwable> T?.asStateError(
    delegate: ((T) -> Pair<ErrorMessage?, String?>)? = null
): State.Error {
    var message: String? = this?.message
    var messageRes: Int? = null
    var errorCode: String? = null

    this?.let { delegate?.invoke(this) }?.let { pair ->
        message = pair.first?.message
        messageRes = pair.first?.messageRes
        errorCode = pair.second
    }

    return State.Error(
        message = message,
        messageRes = messageRes,
        errorCode = errorCode,
        throwable = this
    )
}