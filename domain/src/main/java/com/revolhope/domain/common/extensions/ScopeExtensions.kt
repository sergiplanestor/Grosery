package com.revolhope.domain.common.extensions

inline fun <T, R> T?.letOrThrow(t: Throwable? = null, block: (T) -> R): R {
    this?.let { return block(this) } ?: throw t ?: RuntimeException()
}

inline fun <T> T.applyIf(condition: Boolean, block: T.() -> Unit): T =
    this.applyIf({ condition }, block)

inline fun <T> T.applyIf(predicate: T.() -> Boolean, block: T.() -> Unit): T = apply {
    if (predicate.invoke(this)) block()
}