package com.revolhope.domain.common.model

import androidx.annotation.StringRes
import com.revolhope.domain.R
import com.revolhope.domain.common.extensions.EMPTY_STRING

sealed class NetworkError(
    val errorCode: String,
    @StringRes val messageResource: Int
) {

    companion object {
        // Public const
        const val FIREBASE_USER_NOT_FOUND = "ERROR_USER_NOT_FOUND"
        const val FIREBASE_TIMEOUT = "ERROR_TIMEOUT"

        // Public methods
        fun fromCode(code: String?): NetworkError =
            when (code) {
                FIREBASE_USER_NOT_FOUND -> UserNotFound
                FIREBASE_TIMEOUT -> Timeout
                else -> Unknown
            }

        fun errorMessage(code: String?, isDefaultErrorEnabled: Boolean = true): Int? =
            fromCode(code).takeIf { it !is Unknown || isDefaultErrorEnabled }?.messageResource
    }

    object UserNotFound : NetworkError(FIREBASE_USER_NOT_FOUND, R.string.error_auth_user_not_found)

    object Timeout : NetworkError(FIREBASE_TIMEOUT, R.string.error_firebase_timeout)

    object Unknown : NetworkError(EMPTY_STRING, R.string.error_default)
}
