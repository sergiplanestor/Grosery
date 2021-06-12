package com.revolhope.domain.common.extensions

import java.util.*

fun randomId() = UUID.randomUUID().toString().replace("-", "")

inline fun <reified T> T?.withDefault(defaultValue: T): T = this ?: defaultValue

inline fun <reified T> T?.withDefault(crossinline block: () -> T): T = this ?: block()