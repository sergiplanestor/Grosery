package com.revolhope.domain.common.extensions

import android.util.Log
import com.revolhope.domain.BuildConfig
import com.revolhope.domain.feature.analytics.usecase.ReportCrashUseCase
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

const val DEFAULT_TAG = "Grocery"
const val ERROR_EMPTY_THROWABLE = ""
const val ERROR_EMPTY_MESSAGE = ""

inline fun <reified T> verbose(
    tag: String? = T::class.simpleName,
    message: String? = null,
) {
    log(Log.VERBOSE, tag, message)
}

fun <T : Any> T.verbose(
    tag: String? = this::class.simpleName,
    message: String? = null,
) {
    log(Log.VERBOSE, tag, message)
}

inline fun <reified T> debug(
    tag: String? = T::class.simpleName,
    message: String? = null,
    throwable: Throwable? = null
) {
    log(Log.DEBUG, tag, message, throwable)
}

fun <T : Any> T.debug(
    tag: String? = this::class.simpleName,
    message: String? = null,
    throwable: Throwable? = null
) {
    log(Log.DEBUG, tag, message, throwable)
}

fun report(
    tag: String? = null,
    message: String? = null,
    throwable: Throwable? = null,
    isReportCrashlytics: Boolean = true
) {
    log(Log.ERROR, tag, message, throwable, isReportCrashlytics)
}

fun log(
    level: Int,
    tag: String?,
    message: String? = null,
    throwable: Throwable? = null,
    isReportCrashlytics: Boolean = level == Log.ERROR
) {
    if (BuildConfig.DEBUG) {
        Log.println(
            level,
            tag ?: DEFAULT_TAG,
            buildMessage(isError = level == Log.ERROR, message, throwable)
        )
    }
    if (isReportCrashlytics) {
        GlobalScope.launch(Dispatchers.IO) {
            ReportCrashHelper().safeUseCase?.run {
                invoke(
                    scope = this@launch,
                    requestParams = ReportCrashUseCase.RequestParams(
                        throwable = throwable ?: RuntimeException("Unknown exception...")
                    )
                )
            } ?: Log.println(
                level,
                tag ?: DEFAULT_TAG,
                buildMessage(
                    isError = true,
                    "Error can not be record to firebase...",
                    throwable
                )
            )
        }
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

class ReportCrashHelper @Inject constructor() {
    @Inject
    lateinit var useCase: ReportCrashUseCase

    val safeUseCase: ReportCrashUseCase? get() = if (::useCase.isInitialized) useCase else null
}