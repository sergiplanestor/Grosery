package com.revolhope.domain.common.extensions

import android.os.Handler
import android.os.Looper
import androidx.core.os.postDelayed
import java.util.UUID

fun randomId() = UUID.randomUUID().toString().replace("-", "")

inline fun delay(
    duration: Long,
    isActionOnMain: Boolean = true,
    crossinline action: () -> Unit
) {
    Handler(
        if (isActionOnMain) {
            Looper.getMainLooper()
        } else {
            Looper.myLooper() ?: Looper.getMainLooper()
        }
    ).postDelayed(delayInMillis = duration, action = action)
}

suspend fun safeSuspendedCatchNoConsumed(
    catch: (suspend Throwable.() -> Unit)? = null
): suspend (Throwable.() -> Boolean) = {
    catch?.invoke(this)
    false
}

suspend inline fun safeSuspendedRun(
    noinline catch: suspend Throwable.() -> Boolean = { false },
    noinline finally: suspend () -> Unit = {},
    crossinline block: suspend () -> Unit
) {
    try {
        block.invoke()
    } catch (t: Throwable) {
        if (!t.catch()) report(throwable = t)
    } finally {
        finally()
    }
}

inline fun safeRunNoReturn(
    catch: Throwable.() -> Boolean = { false },
    finally: () -> Unit = {},
    noinline block: () -> Unit
) {
    safe(catch, finally = { finally() }) {
        block.invoke()
    }
}

inline fun <T, reified R> T.safeRunOrNull(
    catch: Throwable.() -> Boolean = { false },
    finally: T.(R?) -> Unit = {},
    crossinline block: T.() -> R
): R? = safe(
    catch = catch,
    finally = { this@safeRunOrNull.finally(this) }) { this@safeRunOrNull.block() }

inline fun <T, reified R> T.safeRunOrDefault(
    default: R,
    catch: Throwable.() -> Boolean = { false },
    finally: T.(R?) -> Unit = {},
    crossinline block: T.() -> R
): R = safeRunOrNull(catch, finally, block) ?: default

/**
 * Try-catch lambda method. It encapsulates logic to send crashlytics report in case of error.
 * @param block lambda performing code to be run safely.
 * @param catch lambda to run in case of [Throwable]. It will return if catch block have been
 * consumed or not.
 * @param finally lambda to run finally.
 */
inline fun <reified R> safe(
    catch: Throwable.() -> Boolean = { false },
    finally: R?.() -> Unit = {},
    noinline block: () -> R
): R? {
    val result = runCatching { block.invoke() }
    if (result.isFailure && result.exceptionOrNull()?.catch() != true) {
        report(tag = block::class.java.simpleName, throwable = result.exceptionOrNull())
    }
    result.getOrNull().finally()
    return result.getOrNull()
}

inline fun <reified T> T?.withDefault(defaultValue: T): T = this ?: defaultValue

inline fun <reified T> T?.withDefault(crossinline block: () -> T): T = this ?: block()