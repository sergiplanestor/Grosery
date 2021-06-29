package com.revolhope.presentation.library.extensions

import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.util.TypedValue
import androidx.annotation.StringRes
import com.revolhope.domain.common.extensions.EMPTY_STRING
import com.revolhope.domain.common.extensions.safeRunOrNull

// -------------------------------------------------------------------------------------------------
// Int
// -------------------------------------------------------------------------------------------------

inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

fun Int.percent(value: Int): Int = (this * value) / 100

inline fun <reified T> Int.resolveRes(context: Context): T? = safeRunOrNull {
    when (T::class.qualifiedName) {
        String::class.qualifiedName -> {
            context.getString(this)
        }
        Drawable::class.qualifiedName -> {
            context.drawableOf(this)
        }
        Float::class.qualifiedName -> {
            context.dimensionOf(this)
        }
        Int::class.qualifiedName -> {
            context.colorOf(this)
        }
        else -> null
    } as? T
}

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

inline fun <reified T> String.resolveResourceId(context: Context): T? = safeRunOrNull {
    context.resources.getIdentifier(
        this,
        "string",
        context.packageName
    ).resolveRes(context)
}


fun String.remove(toRemove: String?): String = toRemove?.let { replace(it, EMPTY_STRING) } ?: this

fun String?.or(ifBlankValue: String): String = if (isNullOrBlank()) ifBlankValue else this

fun String?.or(context: Context, @StringRes stringRes: Int): String =
    this.or(ifBlankValue = context.getString(stringRes))
