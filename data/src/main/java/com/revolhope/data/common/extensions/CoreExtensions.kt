package com.revolhope.data.common.extensions

import android.util.Base64
import com.google.gson.Gson
import com.revolhope.domain.common.extensions.safeRunOrDefault
import java.security.MessageDigest
import kotlin.reflect.KClass

inline val String.sha1: String
    get() = Base64.encodeToString(
        MessageDigest.getInstance("SHA-1").digest(toByteArray()),
        Base64.DEFAULT
    ).replace(Regex("[\\[\\]\\.#\\\$\\/\\u0000-\\u001F\\u007F]"), "")

inline fun <reified T> T.asJson(): String = Gson().toJson(this)

fun <T : Any> String.fromJsonTo(clazz: KClass<T>): T = Gson().fromJson(this, clazz.java)

inline fun <reified T : Any> String?.fromJsonToSafe(
    clazz: KClass<T>,
    fallbackOnNull: T,
    fallbackOnParseError: T = fallbackOnNull
): T = safeRunOrDefault(fallbackOnParseError) { this?.fromJsonTo(clazz) ?: fallbackOnNull }

inline fun <T, R> Iterable<T>.mapToMutable(transform: (T) -> R): MutableList<R> =
    map(transform).toMutableList()
