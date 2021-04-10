package com.revolhope.presentation.library.extensions

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

inline fun runOnUI(delay: Long = 0L, crossinline action: () -> Unit) =
    Handler(Looper.getMainLooper()).postDelayed(
        { action.invoke() },
        delay
    )

inline fun <T> LifecycleOwner.observe(data: LiveData<T>, crossinline action: (T) -> Unit) =
    data.observe(this, { action.invoke(it) })


inline val Int.dp: Int
    get() = TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
    ).toInt()

fun String?.toBooleanOrNull(): Boolean? =
    when {
        this != null && equals("true", ignoreCase = true) -> true
        this != null && equals("false", ignoreCase = true) -> false
        else -> null
    }

inline val Context.inflater: LayoutInflater get() = LayoutInflater.from(this)

fun Context.drawableFrom(@DrawableRes drawableId: Int): Drawable? =
    ContextCompat.getDrawable(this, drawableId)

fun Context.colorFrom(@ColorRes colorId: Int): Int = ContextCompat.getColor(this, colorId)

fun Drawable?.applyTint(@ColorInt colorInt: Int) =
    this?.setTintList(ColorStateList.valueOf(colorInt))

inline fun <T> CoroutineScope.launchAsync(
    dispatcher: CoroutineContext = Dispatchers.IO,
    crossinline asyncTask: suspend () -> T,
    crossinline onCompleteTask: (T) -> Unit = {}
) = launch { withContext(dispatcher) { asyncTask.invoke() }.also(onCompleteTask) }
