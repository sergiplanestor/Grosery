package com.revolhope.domain.common.extensions

import android.util.Log
import com.revolhope.domain.BuildConfig

const val DEFAULT_TAG = "Grocery"
const val ERROR_EMPTY_THROWABLE = ""
const val ERROR_EMPTY_MESSAGE = ""

// TODO: When logging error -> send report to crashlytics :P

inline fun <reified T> logVerbose(
    tag: String? = T::class.simpleName,
    message: String? = null,
) {
    log(Log.VERBOSE, tag, message)
}

fun <T : Any> T.logVerbose(
    tag: String? = this::class.simpleName,
    message: String? = null,
) {
    log(Log.VERBOSE, tag, message)
}

fun <T : Any> T.logDebug(
    tag: String? = this::class.simpleName,
    message: String? = null,
    throwable: Throwable? = null
) {
    log(Log.DEBUG, tag, message, throwable)
}

fun <T : Any> T.logError(
    tag: String? = this::class.simpleName,
    message: String? = null,
    throwable: Throwable? = null
) {
    log(Log.ERROR, tag, message, throwable)
}

inline fun <reified T> logError(
    tag: String? = T::class.simpleName,
    message: String? = null,
    throwable: Throwable? = null
) {
    log(Log.ERROR, tag, message, throwable)
}

fun log(level: Int, tag: String?, message: String? = null, throwable: Throwable? = null) {
    if (BuildConfig.DEBUG) {
        Log.println(
            level,
            tag ?: DEFAULT_TAG,
            buildMessage(isError = level == Log.ERROR, message, throwable)
        )
    }
}

fun buildMessage(isError: Boolean, message: String?, throwable: Throwable?): String =
    when {
        message != null && throwable == null -> {
            message to if (isError) ERROR_EMPTY_THROWABLE else ""
        }
        message == null && throwable != null -> {
            (throwable::class.simpleName ?: ERROR_EMPTY_MESSAGE) to Log.getStackTraceString(
                throwable
            )
        }
        message != null && throwable != null -> {
            message to Log.getStackTraceString(throwable)
        }
        else /* message == null && throwable == null */ -> {
            ERROR_EMPTY_MESSAGE to if (isError) ERROR_EMPTY_THROWABLE else ""
        }
    }.let {
        "${it.first}${if (it.second.isNotBlank()) "\n${it.second}" else ""}"
    }
