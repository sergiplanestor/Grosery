package com.revolhope.presentation.library.extensions

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.TypedValue
import com.revolhope.domain.common.extensions.EMPTY_STRING

// -------------------------------------------------------------------------------------------------
// Int
// -------------------------------------------------------------------------------------------------

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

// -------------------------------------------------------------------------------------------------
// String
// -------------------------------------------------------------------------------------------------

fun String?.toBooleanOrNull(): Boolean? =
    when {
        this != null && equals("true", ignoreCase = true) -> true
        this != null && equals("false", ignoreCase = true) -> false
        else -> null
    }

fun String?.toBitmap(): Bitmap? =
    Base64.decode(this, Base64.DEFAULT)?.let {
        BitmapFactory.decodeByteArray(it, 0, it.size)
    }

fun String.remove(toRemove: String?): String = toRemove?.let { replace(it, EMPTY_STRING) } ?: this
