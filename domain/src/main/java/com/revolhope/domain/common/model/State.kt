package com.revolhope.domain.common.model

import com.revolhope.domain.common.extensions.EMPTY_STRING

sealed class State<out T> {
    object Loading : State<Nothing>()
    data class Success<out T>(val data: T) : State<T>()
    data class Error(
        val message: String? = null,
        val messageRes: Int? = null,
        val errorCode: String? = null,
        val throwable: Throwable? = null
    ) : State<Nothing>() {

        val isErrorMessageResource: Boolean get() = messageRes != null

        val errorMessageOrEmpty: String
            get() =
                messageRes?.toString() ?: message ?: throwable?.message ?: EMPTY_STRING
    }
}
