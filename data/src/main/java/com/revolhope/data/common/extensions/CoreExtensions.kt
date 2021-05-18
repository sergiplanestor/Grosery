package com.revolhope.data.common.extensions

import android.util.Base64
import com.google.gson.Gson
import java.security.MessageDigest
import kotlin.reflect.KClass

inline val String.sha1: String
    get() = Base64.encodeToString(
        MessageDigest.getInstance("SHA-1").digest(toByteArray()),
        Base64.DEFAULT
    ).replace(Regex("[\\[\\]\\.#\\\$\\/\\u0000-\\u001F\\u007F]"), "")

inline val <reified T> T.json: String
    get() =
        Gson().toJson(this, T::class.java)

fun <T : Any> String.fromJSON(clazz: KClass<T>): T = Gson().fromJson(this, clazz.java)

inline fun <T, R> Iterable<T>.mapToMutable(transform: (T) -> R): MutableList<R> =
    map(transform).toMutableList()
