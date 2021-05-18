package com.revolhope.data.feature.storage.network

import androidx.annotation.StringRes
import com.google.firebase.FirebaseException
import com.revolhope.data.R

enum class FirebaseError(val errorCode: String, @StringRes val resValue: Int) {

    USER_NOT_FOUND("ERROR_USER_NOT_FOUND", R.string.error_auth_user_not_found),
    UNKNOWN(com.google.firebase.FirebaseError.ERROR_USER_NOT_FOUND.toString(), R.string.error_default);

    companion object {
        fun fromCode(code: String?): FirebaseError =
            values().associateBy { it.errorCode }[code] ?: UNKNOWN

        fun resValue(code: String?): Int = fromCode(code).resValue
    }
}
