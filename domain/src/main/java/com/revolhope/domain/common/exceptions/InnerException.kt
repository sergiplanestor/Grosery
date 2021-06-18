package com.revolhope.domain.common.exceptions

import com.revolhope.domain.common.model.State

sealed class InnerException(
    override val message: String? = null,
    open val errorCode: String? = null,
    override val cause: Throwable? = RuntimeException(message.orEmpty())
) : Throwable(message, cause) {

    companion object {
        // Codes
        private const val ON_SUCCESS_MAP_TO__RESULT_FALSE_STATE_CODE = "INNER_EXCEPTION_#1"
        // Messages
        private const val ON_SUCCESS_MAP_TO__RESULT_FALSE_STATE_MSG = "FlowExtensions.kt:\n" +
                "fun <reified T, R> Flow<State<T>>.onSuccessMapTo(crossinline block: () -> State<R>): Flow<State<R>>\n" +
                "Block invoke has returned State.Success(Boolean).data = false, impossible to cast to" +
                "State<R>."
    }

    fun asStateError(
        message: String? = null,
        messageRes: Int? = null,
        errorCode: String? = null
    ): State.Error =
        State.Error(
            message = message ?: this.message,
            messageRes = messageRes,
            errorCode = errorCode ?: this.errorCode
        )


    data class FlowFalseState(
        override val message: String? = null,
        override val errorCode: String? = null
    ) : InnerException(
        message = message ?: ON_SUCCESS_MAP_TO__RESULT_FALSE_STATE_MSG,
        cause = IllegalStateException(message),
        errorCode = errorCode ?: ON_SUCCESS_MAP_TO__RESULT_FALSE_STATE_CODE
    )

}
