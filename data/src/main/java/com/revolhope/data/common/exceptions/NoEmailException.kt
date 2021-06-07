package com.revolhope.data.common.exceptions

data class NoEmailException(
    override val message: String? = null,
    override val cause: Throwable? = null
) : Throwable(message, cause)
